package com.contractar.microservicioimages.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contractar.microservicioimages.services.ImageService;

@RestController
public class ImageController {
	
	@Autowired
	private ImageService imageService;

	@PostMapping("/image/vendible/{vendibleName}/proveedor/{proveedorId}/upload")
	public ResponseEntity<String> handleVendibleImageUpload(@RequestParam("file") MultipartFile file,
			@PathVariable("vendibleName") String vendibleName,
			@PathVariable("proveedorId") Long proveedorId) {
		String fileFullUrl = imageService.saveProveedorVendibleImage(
				file,
				proveedorId,
				vendibleName);
		return new ResponseEntity<String>(fileFullUrl, HttpStatus.OK);
	}
}
