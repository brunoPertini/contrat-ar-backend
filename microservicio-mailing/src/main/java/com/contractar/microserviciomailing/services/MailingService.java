package com.contractar.microserviciomailing.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.MailNotificationResultBody;
import com.contractar.microserviciocommons.mailing.PaymentLinkMailInfo;
import com.contractar.microserviciocommons.mailing.PlanChangeConfirmation;
import com.contractar.microserviciocommons.mailing.ProveedorMessageBody;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.dto.TokenInfoPayload;
import com.contractar.microserviciocommons.dto.TokenType;
import com.contractar.microserviciocommons.mailing.AdminChangeRequestInfo;
import com.contractar.microserviciocommons.mailing.ContactFormBody;
import com.contractar.microserviciocommons.mailing.ForgotPasswordMailInfo;
import com.contractar.microserviciocommons.mailing.LinkMailInfo;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciocommons.mailing.UserDataChangedMailInfo;
import com.contractar.microserviciocommons.mailing.VendibleModificationNotification;
import com.contractar.microserviciomailing.utils.FileReader;

import jakarta.annotation.Nullable;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailingService {

	private JavaMailSender mailSender;

	private Environment env;

	private RestTemplate httpClient;

	@Value("${configServiceUrl}")
	private String configServiceUrl;

	@Value("${securityServiceUrl}")
	private String securityServiceUrl;

	@Value("${site.changePassword.url}")
	private String resetPasswordLink;

	@Value("${mail.contactus}")
	private String contactMail;
	
	@Value("${mail.noReply}")
	private String noReplyEmail;

	@Value("${site.termsAndConditions.link}")
	private String termsAndConditionsUrl;

	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public MailingService(Environment env, JavaMailSender mailSender, RestTemplate httpClient) {
		this.env = env;
		this.mailSender = mailSender;
		this.httpClient = httpClient;
	}

	public void sendEmail(String mailAddress, String title, String bodyMessage, boolean isMultiPart,
			@Nullable String replyTo) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		
        String messageId = "<" + UUID.randomUUID() + "@contratar.com.ar>";
        message.setHeader("Message-ID", messageId);

        message.setHeader("Precedence", "bulk");
        message.setHeader("X-Auto-Response-Suppress", "All");
        message.setHeader("X-Priority", "3");
        message.setHeader("X-Mailer", "Contrat-Ar System Mailer");

		MimeMessageHelper helper = new MimeMessageHelper(message, isMultiPart, "UTF-8");

		helper.setFrom(env.getProperty("spring.mail.username"));
		helper.setTo(mailAddress);
		helper.setSubject(title);

		helper.setText(bodyMessage, isMultiPart);

		if (replyTo != null) {
			helper.setReplyTo(replyTo);
		}

		mailSender.send(message);
	}

	private String requestBackupPasswordToken(UserDataChangedMailInfo mailInfo) {
		final String url = securityServiceUrl + SecurityControllerUrls.TOKEN_BASE_PATH;

		TokenInfoPayload body = new TokenInfoPayload(mailInfo.getToAddress(), TokenType.reset_password,
				mailInfo.getUserId(), mailInfo.getRoleName());

		return httpClient.postForObject(url, body, String.class);
	}

	public void sendRegistrationLinkEmail(LinkMailInfo mailInfo) {
		try {
			String accountConfirmationUrl = env.getProperty("signup.verificationLink.url") + "?token="
					+ mailInfo.getToken() + "&email=" + mailInfo.getToAddress();

			String emailContent = new FileReader().readFile("/static/registration-link-template.html")
					.replaceAll("\\$\\{registrationLink\\}", accountConfirmationUrl)
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));

			this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.signup.success.title"), emailContent, true,
					null);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void sendWelcomeEmail(MailInfo mailInfo) {
		try {
			String signinUrl = env.getProperty("site.signin.url");

			String emailContent = new FileReader().readFile("/static/welcome-email-template.html")
					.replaceAll("\\$\\{siteLink\\}", signinUrl)
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));

			this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.signup.result.title"), emailContent, true,
					null);
		} catch (IOException | MessagingException e) {
			System.out.println(e.getMessage());
		}
	}

	public void sendTwoFactorAuthenticationEmail(TwoFactorAuthMailInfo mailInfo)
			throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/two-factor-code-email.html")
				.replaceAll("\\$\\{code\\}", String.valueOf(mailInfo.getCode()))
				.replaceAll("\\$\\{userName\\}", mailInfo.getFullUserName())
				.replaceAll("\\$\\{expiresInMinutes\\}", String.valueOf(mailInfo.getExpiresInMinutes()))
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{changePasswordLink\\}", env.getProperty("site.changePassword.url"))
				.replaceAll("\\$\\{contactUsLink\\}", env.getProperty("site.contactUs.link"));

		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.2fa.title"), emailContent, true, null);
	}

	public void sendForgotPasswordEmail(ForgotPasswordMailInfo mailInfo) throws IOException, MessagingException {
		String resetPasswordFinalLink = resetPasswordLink.replaceAll("\\{token\\}", mailInfo.getToken());
		String emailContent = new FileReader().readFile("/static/forgot-password-template.html")
				.replaceAll("\\$\\{link\\}", resetPasswordFinalLink)
				.replaceAll("\\$\\{userName\\}", String.valueOf(mailInfo.getFullUserName()))
				.replaceAll("\\$\\{expiresInMinutes\\}", String.valueOf(mailInfo.getExpiresInMinutes()))
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));

		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.forgotPassword.title"), emailContent, true, null);
	}

	public String sendUserDataChangeSuccessEmail(UserDataChangedMailInfo mailInfo)
			throws IOException, MessagingException {
		String parsedAttributesList = mailInfo.getFieldsList().size() == 1
				? getMessageTag("fields.usuario." + mailInfo.getFieldsList().get(0))
				: mailInfo.getFieldsList().stream().reduce("",
						(acum, attribute) -> acum + "y" + getMessageTag("fields.usuario." + attribute));

		String backupToken = this.requestBackupPasswordToken(mailInfo);

		String parsedChangePasswordUrl = env.getProperty("site.changePassword.url").replaceAll("\\{token\\}",
				backupToken);

		String emailContent = new FileReader().readFile("/static/user-data-change-success.html")
				.replaceAll("\\$\\{userName\\}", mailInfo.getUserName())
				.replaceAll("\\$\\{attributesListTemplate\\}", parsedAttributesList)
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{changePasswordLink\\}", parsedChangePasswordUrl)
				.replaceAll("\\$\\{contactUsLink\\}", env.getProperty("site.contactUs.link"));

		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.forgotPassword.title"), emailContent, true, null);

		return backupToken;
	}

	public void sendPlanChangeConfirmationEmail(PlanChangeConfirmation mailInfo)
			throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/plan-change-confirmation.html")
				.replaceAll("\\$\\{userName\\}", mailInfo.getUserName())
				.replaceAll("\\$\\{currentPlan\\}", mailInfo.getDestinyPlan())
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));

		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.changePlan.title"), emailContent, true, null);
	}

	public void sendPaymentLinkEmail(PaymentLinkMailInfo mailInfo) throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/payment_started.html")
				.replaceAll("\\$\\{userName\\}", mailInfo.getUserName() != null ? (" " + mailInfo.getUserName()) : "")
				.replaceAll("\\$\\{paymentLink\\}", mailInfo.getPaymentLink())
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));

		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.paymentStarted.title"), emailContent, true, null);

	}

	public void sendSignupResultNotification(MailNotificationResultBody body) throws IOException, MessagingException {

		if (body.isResult()) {
			this.sendWelcomeEmail(body);
		} else {
			String emailContent = new FileReader().readFile("/static/signup_result_notification.html")
					.replaceAll("\\$\\{userName\\}", body.getUserName())
					.replaceAll("\\$\\{resultVerb\\}", getMessageTag("mails.signup.result.error"))
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
					.replaceAll("\\$\\{contactMail\\}", "mailto:" + contactMail);

			this.sendEmail(body.getToAddress(), getMessageTag("mails.signup.result.title"), emailContent, true, null);
		}
	}

	public void sendPostUpdateResultNotification(VendibleModificationNotification body)
			throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/post-state-change-notification.html")
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{userName\\}", body.getUserName())
				.replaceAll("\\$\\{vendibleName\\}", body.getVendibleName())
				.replaceAll("\\$\\{result\\}",
						body.isResult() ? getMessageTag("mails.signup.result.success")
								: getMessageTag("mails.signup.result.error"))
				.replaceAll("\\$\\{termsAndConditionsLink\\}", termsAndConditionsUrl);

		this.sendEmail(body.getToAddress(), getMessageTag("mails.post.result.title"), emailContent, true, null);

	}

	public void sendContactFormEmail(ContactFormBody body) throws IOException, MessagingException {
		String phoneText = "";

		if (body.getPhoneField() != null && StringUtils.hasLength(body.getPhoneField())) {

			phoneText += "<p>Teléfono de contacto: <b>" + body.getPhoneField() + "</b></p>";
		}

		String emailContent = new FileReader().readFile("/static/contact_form_mail.html")
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{fromName\\}", body.getFromName())
				.replaceAll("\\$\\{fromEmail\\}", body.getFromEmail()).replaceAll("\\$\\{telOptionalText\\}", phoneText)
				.replaceAll("\\$\\{messageText\\}", body.getMessage());

		this.sendEmail(contactMail, getMessageTag("mails.contactForm.title"), emailContent, true, body.getFromEmail());

	}
	
	public void sendAdminChangeRequestInfo(AdminChangeRequestInfo mailInfo) throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/admin_notification_mail.html")
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{requestEntity\\}", mailInfo.getRequestEntity())
				.replaceAll("\\$\\{requestId\\}", mailInfo.getChangeRequestId().toString());
		
		this.sendEmail(mailInfo.getToAddress() , getMessageTag("mails.admin.notification.changeRequest.title"), emailContent, true, null);
	}
	
	public void sendMessageToProveedor(ProveedorMessageBody body) throws IOException, MessagingException {
		String emailContent = new FileReader().readFile("/static/send_message_to_proveedor.html")
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{clienteMail\\}", body.getClienteMail())
				.replaceAll("\\$\\{vendibleName\\}", body.getVendibleName())
				.replaceAll("\\$\\{message\\}", body.getMessage());
		
		this.sendEmail(body.getToAddress(), getMessageTag("mails.proveedorMessage.title"), emailContent, true, body.getClienteMail());
	}

}
