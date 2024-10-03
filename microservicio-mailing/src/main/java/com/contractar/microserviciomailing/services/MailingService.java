package com.contractar.microserviciomailing.services;

import java.io.IOException;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.mailing.RegistrationLinkMailInfo;
import com.contractar.microserviciomailing.utils.FileReader;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailingService {

	private JavaMailSender mailSender;

	private Environment env;

	public MailingService(Environment env, JavaMailSender mailSender) {
		this.env = env;
		this.mailSender = mailSender;
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

	public void sendRegistrationLinkEmail(RegistrationLinkMailInfo mailInfo) {
		try {
			String accountConfirmationUrl = env.getProperty("signup.verificationLink.url")+"?token="+mailInfo.getToken()+"&email="+mailInfo.getToAddress();
			
			String emailContent = new FileReader()
					.readFile("/static/registration-link-template.html")
					.replaceAll("\\$\\{registrationLink\\}", accountConfirmationUrl)
					.replaceAll("\\$\\{cdnUrl\\}", env.getProperty("cdn.url"));
			
			this.sendEmail(mailInfo.getToAddress(), "¡Bienvenido a Contract-Ar!", emailContent, true);
		} catch(IOException | MessagingException e) {
			System.out.println(e.getMessage());
		}
	}

}
