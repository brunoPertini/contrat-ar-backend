package com.contractar.microserviciovendible.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciousuario.models.VendibleCategory;
import com.contractar.microserviciovendible.services.VendibleService;

import jakarta.validation.Valid;

@Controller
public class VendibleController {
	@Autowired
	private VendibleService vendibleService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping(VendiblesControllersUrls.GET_VENDIBLE)
	public ResponseEntity<VendibleDTO> getVendibleByParam(@RequestParam(required = false) Long vendibleId) {
		try {
			VendibleDTO vendible = vendibleService.findById(vendibleId);
			return new ResponseEntity<VendibleDTO>(vendible, HttpStatusCode.valueOf(200));
		} catch (VendibleNotFoundException e) {
			return new ResponseEntity(HttpStatusCode.valueOf(404));
		}
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
	
	@PostMapping(VendiblesControllersUrls.GET_CATEGORY_HIERACHY)
	public ResponseEntity<List<String>> getVendibleCategoryHierachy(@RequestBody @Valid VendibleCategory body) {
		return new ResponseEntity<List<String>>(vendibleService.getCategoryHierachy(body), HttpStatus.OK);
	}
	
}
