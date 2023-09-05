package com.contractar.microserviciovendible.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.services.VendibleService;

@Controller
public class VendibleController {
	@Autowired
	private VendibleService vendibleService;
	
	@SuppressWarnings("unchecked")
	@GetMapping(VendiblesControllersUrls.GET_VENDIBLE)
	public ResponseEntity<Vendible> getVendibleByParam(@RequestParam(required = false) Long vendibleId) {
		Optional<Vendible> vendibleOpt = vendibleService.findById(vendibleId);
		return vendibleOpt.isPresent() ? new ResponseEntity<Vendible>(vendibleOpt.get(), HttpStatusCode.valueOf(200)) 
				: new ResponseEntity(HttpStatusCode.valueOf(404));
	}
	
	@DeleteMapping(VendiblesControllersUrls.DELETE_VENDIBLE)
	public ResponseEntity<Void> deleteById(@PathVariable("vendibleId") Long id) throws VendibleNotFoundException {
		vendibleService.deleteById(id);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(204));
	}
	
	@GetMapping(VendiblesControllersUrls.GET_VENDIBLE_TYPE)
	public ResponseEntity<String> getVendibleType(@PathVariable("vendibleId") Long id) {
		return new ResponseEntity<String>(vendibleService.getVendibleTypeById(id), HttpStatusCode.valueOf(200));
	}
}
