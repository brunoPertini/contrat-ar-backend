package com.contractar.microserviciousuario.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceCreate;
import com.contractar.microserviciocommons.exceptions.CantCreatePromotion;
import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.services.PromotionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/promotion")
public class PromotionsController {
	private PromotionService service;

	public PromotionsController(PromotionService service) {
		this.service = service;
	}
	
	@PostMapping(PromotionControllerUrls.PROMOTION_INSTANCE_BASE_URL)
	public ResponseEntity<PromotionInstance> createPromotionInstance(@RequestBody @Valid PromotionInstanceCreate createDTO) throws CantCreatePromotion {
		return new ResponseEntity<>(this.service.createPromotionInstance(createDTO), HttpStatus.CREATED);
	}
		
	@GetMapping
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<>(this.service.findAll(), HttpStatus.OK);
	}
}
