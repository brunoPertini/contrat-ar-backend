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
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.mailing.MailInfo;
import com.contractar.microserviciocommons.mailing.MailNotificationResultBody;
import com.contractar.microserviciocommons.mailing.PaymentLinkMailInfo;
import com.contractar.microserviciocommons.mailing.PlanChangeConfirmation;
import com.contractar.microserviciocommons.mailing.AdminChangeRequestInfo;
import com.contractar.microserviciocommons.mailing.ContactFormBody;
import com.contractar.microserviciocommons.mailing.ForgotPasswordMailInfo;
import com.contractar.microserviciocommons.mailing.LinkMailInfo;
import com.contractar.microserviciocommons.mailing.TwoFactorAuthMailInfo;
import com.contractar.microserviciocommons.mailing.UserDataChangedMailInfo;
import com.contractar.microserviciocommons.mailing.VendibleModificationNotification;

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
	ResponseEntity<String> sendUserDataChangeSuccessfulEmail(@RequestBody @Valid UserDataChangedMailInfo body) throws IOException, MessagingException {
		return new ResponseEntity<>(service.sendUserDataChangeSuccessEmail(body), HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.PLAN_CHANGE_SUCCESS_EMAIL)
	ResponseEntity<Void> sendPlanChangeSuccessEmail(@RequestBody @Valid PlanChangeConfirmation body) throws IOException, MessagingException {
		service.sendPlanChangeConfirmationEmail(body);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.PAYMENT_LINK_EMAIL)
	ResponseEntity<Void> sendPaymentLinkEmil(@RequestBody @Valid PaymentLinkMailInfo mailInfo) throws IOException, MessagingException {
		service.sendPaymentLinkEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.SIGNUP_RESULT_NOTIFICATION)
	ResponseEntity<Void> sendSignupResultMail(@RequestBody @Valid MailNotificationResultBody mailInfo) throws IOException, MessagingException {
		service.sendSignupResultNotification(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.POST_RESULT_NOTIFICATION)
	ResponseEntity<Void> sendPostUpdateResultMail(@RequestBody @Valid VendibleModificationNotification mailInfo) throws IOException, MessagingException {
		service.sendPostUpdateResultNotification(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(UsersControllerUrls.CONTACT_FORM_EMAIL)
	ResponseEntity<Void> sendContactFormEmail(@RequestBody @Valid ContactFormBody mailInfo) throws IOException, MessagingException {
		service.sendContactFormEmail(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping(AdminControllerUrls.ADMIN_SEND_NEW_CHANGE_REQUEST_EMAIL)
	ResponseEntity<Void> sendChangeRequestEmail(@RequestBody @Valid AdminChangeRequestInfo mailInfo) throws IOException, MessagingException {
		service.sendAdminChangeRequestInfo(mailInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
