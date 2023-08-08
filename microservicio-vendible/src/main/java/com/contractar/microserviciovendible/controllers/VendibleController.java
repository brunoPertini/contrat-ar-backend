package com.contractar.microserviciovendible.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.exceptions.VendibleNotFoundException;
import com.contractar.microserviciovendible.services.VendibleService;

@Controller
public class VendibleController {
	@Autowired
	private VendibleService vendibleService;
	
	@DeleteMapping(VendiblesControllersUrls.DELETE_VENDIBLE)
	public ResponseEntity<Void> deleteById(@PathVariable("vendibleId") Long id) throws VendibleNotFoundException {
		vendibleService.deleteById(id);
		return new ResponseEntity<Void>(HttpStatusCode.valueOf(204));
	}
}
