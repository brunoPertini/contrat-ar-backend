package com.contractar.microserviciomailing.services;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailingService {
	
	private JavaMailSender mailSender;
	
	private Environment env;
	
	public MailingService(Environment env, JavaMailSender mailSender) {
		this.env = env;
		this.mailSender = mailSender;
	}
	
	public void sendEmail(String mailAddress, String title, String bodyMessage) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(env.getProperty("spring.mail.username"));
		message.setTo(mailAddress);
		message.setSubject(title);
		message.setText(bodyMessage);
		
		mailSender.send(message);
	}

}
