package com.contractar.microserviciomailing.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciomailing.services.MailingService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.ForgotPasswordMailInfo;
import com.contractar.microserviciocommons.mailing.LinkMailInfo;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciocommons.mailing.UserDataChangedMailInfo;

@RestController
public class MailingController {

	@Autowired
	private MailingService service;

	@PostMapping(UsersControllerUrls.SEND_REGISTRATION_LINK_EMAIL)
	ResponseEntity<Void> sendRegistrationLinkEmail(@RequestBody LinkMailInfo mailInfo) {
		service.sendRegistrationLinkEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	@PostMapping(UsersControllerUrls.SIGNUP_OK_EMAIL)
	ResponseEntity<Void> sendWelcomeEmail(@RequestBody MailInfo mailInfo) {
		service.sendWelcomeEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);

	}
	
	@PostMapping(SecurityControllerUrls.SEND_2FA_MAIL)
	ResponseEntity<Void> send2faMail(@RequestBody @Valid TwoFactorAuthMailInfo body) throws IOException, MessagingException {
		service.sendTwoFactorAuthenticationEmail(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.FORGOT_PASSWORD_EMAIL)
	ResponseEntity<Void> sendForgotPasswordEmail(@RequestBody @Valid ForgotPasswordMailInfo body) throws IOException, MessagingException{
		service.sendForgotPasswordEmail(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.USER_FIELD_CHANGE_SUCCESS)
	ResponseEntity<Void> sendUserDataChangeSuccessfulEmail(@RequestBody @Valid UserDataChangedMailInfo body) throws IOException, MessagingException {
		service.sendUserDataChangeSuccessEmail(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
