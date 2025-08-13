package com.contractar.microserviciousuario.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.dto.UserPromotionDTO;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceCreate;
import com.contractar.microserviciocommons.dto.usuario.PromotionInstanceUpdateDTO;
import com.contractar.microserviciocommons.exceptions.CantCreatePromotion;
import com.contractar.microserviciocommons.exceptions.proveedores.SuscriptionNotFound;
import com.contractar.microserviciousuario.models.Promotion;
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
	public ResponseEntity<PromotionInstance> createPromotionInstance(@RequestBody @Valid PromotionInstanceCreate createDTO) throws CantCreatePromotion, SuscriptionNotFound {
		return new ResponseEntity<>(this.service.createPromotionInstance(createDTO), HttpStatus.CREATED);
	}
		
	@GetMapping
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<>(this.service.findAll(), HttpStatus.OK);
	}
	
	@GetMapping(PromotionControllerUrls.PROMOTION_BY_ID)
	public ResponseEntity<List<Promotion>> findUserApplicablePromotions(@PathVariable Long userId) {
		return new ResponseEntity<>(this.service.findAllAplicable(userId), HttpStatus.OK);
	}
	
	@GetMapping(PromotionControllerUrls.PROMOTION_INSTANCE_BY_ID)
	public ResponseEntity<UserPromotionDTO> getInstanceById(@PathVariable  Long suscriptionId) {
		UserPromotionDTO response = service.findUserPromotion(suscriptionId);
		
		return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatusCode.valueOf(404));
	}
	
	@PutMapping(PromotionControllerUrls.PROMOTION_INSTANCE_FULL_URL)
    public ResponseEntity<?> updatePromotionInstance(
            @PathVariable Long promotionId,
            @PathVariable Long suscriptionId,
            @RequestBody @Valid PromotionInstanceUpdateDTO dto) {
        return service.updatePromotionInstanceExpirationDate(promotionId, suscriptionId, dto.getExpirationDate())
                .map(updated -> new ResponseEntity<>(updated, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
	
}