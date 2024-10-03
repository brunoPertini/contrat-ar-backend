package com.contractar.microserviciomailing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciomailing.services.MailingService;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.mailing.RegistrationLinkMailInfo;

@RestController
public class MailingController {
	
	@Autowired
	private MailingService service;

	@PostMapping(UsersControllerUrls.SEND_REGISTRATION_LINK_EMAIL)
	ResponseEntity<Void> sendEmail(@RequestBody RegistrationLinkMailInfo mailInfo) {
		try {
			service.sendRegistrationLinkEmail(mailInfo);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (MailSendException | MailParseException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}

}
