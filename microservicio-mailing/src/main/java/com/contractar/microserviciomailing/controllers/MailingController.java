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

@RestController
public class MailingController {
	
	@Autowired
	private MailingService service;

	@PostMapping("/mail")
	ResponseEntity<Void> sendEmail(@RequestBody MailInfo mailInfo) {
		try {
			service.sendEmail(mailInfo.getToAddress(), mailInfo.getTitle(), mailInfo.getMessage());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (MailSendException | MailParseException e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
	}

}
