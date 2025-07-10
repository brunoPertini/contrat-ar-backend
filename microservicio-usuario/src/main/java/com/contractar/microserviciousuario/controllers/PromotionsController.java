package com.contractar.microserviciousuario.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciousuario.services.PromotionService;

@RestController
@RequestMapping("/promotion")
public class PromotionsController {
	private PromotionService service;

	public PromotionsController(PromotionService service) {
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<>(this.service.findAll(), HttpStatus.OK);
	}
}
