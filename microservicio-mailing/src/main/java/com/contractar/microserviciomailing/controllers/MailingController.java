package com.contractar.microserviciomailing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciomailing.services.MailingService;

import jakarta.servlet.http.HttpServlet;
import jakarta.validation.Valid;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.RegistrationLinkMailInfo;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;

@RestController
public class MailingController {

	@Autowired
	private MailingService service;

	@PostMapping(UsersControllerUrls.SEND_REGISTRATION_LINK_EMAIL)
	ResponseEntity<Void> sendRegistrationLinkEmail(@RequestBody RegistrationLinkMailInfo mailInfo) {
		service.sendRegistrationLinkEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PostMapping(UsersControllerUrls.SIGNUP_OK_EMAIL)
	ResponseEntity<Void> sendWelcomeEmail(@RequestBody MailInfo mailInfo) {
		service.sendWelcomeEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	@PostMapping(SecurityControllerUrls.SEND_2FA_MAIL)
	ResponseEntity<Void> send2faMail(@RequestBody @Valid TwoFactorAuthMailInfo body) {
		
	}

}
