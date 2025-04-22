package com.contractar.microserviciousuario.admin.utils;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.mailing.MailNotificationResultBody;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.AdminService;
import com.contractar.microserviciousuario.models.Usuario;

public class UsuarioActiveRejectChangeRequestStrategy implements ChangeRequestStrategy {

	@Override
	public void run(ChangeRequest request, AdminService adminService) {
		Usuario relatedUser;

		try {
			relatedUser = adminService.findUserById(request.getSourceTableIds().get(0));
		} catch (UserNotFoundException e) {
			relatedUser = null;
		}
		
		MailNotificationResultBody mailBody = new MailNotificationResultBody(relatedUser.getEmail(), false, relatedUser.getName());
		adminService.sendEmail(UsersControllerUrls.SIGNUP_RESULT_NOTIFICATION, mailBody);

	}

}
