package com.contractar.microservicioconfig.i18n.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microservicioconfig.i18n.config.MessageConfig;

@RestController
@RequestMapping("/i18n")
public class I18nController {
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private MessageConfig messageConfig;
	
	@GetMapping("/{tag}")
	public ResponseEntity<String> findMessage(@PathVariable("tag") String tagId) {
		try {
			return new ResponseEntity<String>(messageSource.getMessage(tagId, null, messageConfig.getLocale()), HttpStatus.OK);
		} catch (NoSuchMessageException e) {
			return new ResponseEntity<String>(tagId, HttpStatus.OK);
		}
	}
}
