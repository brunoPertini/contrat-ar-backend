package com.contractar.microserviciomailing.services;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.ForgotPasswordMailInfo;
import com.contractar.microserviciocommons.mailing.LinkMailInfo;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciomailing.utils.FileReader;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailingService {

	private JavaMailSender mailSender;

	private Environment env;
	
	private RestTemplate httpClient;
	
	@Value("${configServiceUrl}")
	private String configServiceUrl;
	
	@Value("${site.changePassword.url}")
	private String resetPasswordLink;
	
	private String getMessageTag(String tagId) {
		final String fullUrl = configServiceUrl + "/i18n/" + tagId;
		return httpClient.getForObject(fullUrl, String.class);
	}

	public MailingService(Environment env, JavaMailSender mailSender, RestTemplate httpClient) {
		this.env = env;
		this.mailSender = mailSender;
		this.httpClient = httpClient;
	}

	public void sendEmail(String mailAddress, String title, String bodyMessage, boolean isMultiPart)
			throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, isMultiPart, "UTF-8");

		helper.setFrom(env.getProperty("spring.mail.username"));
		helper.setTo(mailAddress);
		helper.setSubject(title);

		helper.setText(bodyMessage, isMultiPart);

		mailSender.send(message);
	}

	public void sendRegistrationLinkEmail(LinkMailInfo mailInfo) {
		try {
			String accountConfirmationUrl = env.getProperty("signup.verificationLink.url")+"?token="+mailInfo.getToken()+"&email="+mailInfo.getToAddress();
			
			String emailContent = new FileReader()
					.readFile("/static/registration-link-template.html")
					.replaceAll("\\$\\{registrationLink\\}", accountConfirmationUrl)
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));
			
			this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.signup.success.title"), emailContent, true);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void sendWelcomeEmail(MailInfo mailInfo) {
		try {
			String signinUrl = env.getProperty("site.signin.url");
			
			String emailContent = new FileReader()
					.readFile("/static/welcome-email-template.html")
					.replaceAll("\\$\\{siteLink\\}", signinUrl)
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));
			
			this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.signup.confirm.success.title"), emailContent, true);  
		} catch(IOException | MessagingException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void sendTwoFactorAuthenticationEmail(TwoFactorAuthMailInfo mailInfo) throws IOException, MessagingException {
		String emailContent = new FileReader()
				.readFile("/static/two-factor-code-email.html")			
				.replaceAll("\\$\\{code\\}", String.valueOf(mailInfo.getCode()))
				.replaceAll("\\$\\{userName\\}", mailInfo.getFullUserName())
				.replaceAll("\\$\\{expiresInMinutes\\}", String.valueOf(mailInfo.getExpiresInMinutes()))
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"))
				.replaceAll("\\$\\{changePasswordLink\\}", env.getProperty("site.changePassword.url"))
				.replaceAll("\\$\\{contactUsLink\\}", env.getProperty("site.contactUs.link"));
		
		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.2fa.title"), emailContent, true);  
	}
	
	public void sendForgotPasswordEmail(ForgotPasswordMailInfo mailInfo) throws IOException, MessagingException {
		String resetPasswordFinalLink = resetPasswordLink.replaceAll("\\{token\\}", mailInfo.getToken());
		String emailContent = new FileReader()
				.readFile("/static/forgot-password-template.html")
				.replaceAll("\\$\\{link\\}", resetPasswordFinalLink)
				.replaceAll("\\$\\{userName\\}", String.valueOf(mailInfo.getFullUserName()))
				.replaceAll("\\$\\{expiresInMinutes\\}", String.valueOf(mailInfo.getExpiresInMinutes()))
				.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));
		
		this.sendEmail(mailInfo.getToAddress(), getMessageTag("mails.forgotPassword.title"), emailContent, true); 
	}

}
