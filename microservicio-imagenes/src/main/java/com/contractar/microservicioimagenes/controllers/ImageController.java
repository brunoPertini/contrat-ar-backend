package com.contractar.microservicioimagenes.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contractar.microserviciocommons.constants.controllers.ImagenesControllerUrls;
import com.contractar.microserviciocommons.infra.ExceptionFactory;
import com.contractar.microservicioimagenes.exceptions.ImageUploadException;
import com.contractar.microservicioimagenes.services.ImageService;

@RestController
public class ImageController {

	@Autowired
	private ImageService imageService;

	@PostMapping(ImagenesControllerUrls.UPLOAD_VENDIBLE_IMAGE_URL)
	public ResponseEntity<?> handleVendibleImageUpload(@RequestParam("file") MultipartFile file,
			@PathVariable("vendibleName") String vendibleName, @PathVariable("proveedorId") Long proveedorId)
			throws ImageUploadException {
		try {
			String fileFullUrl = imageService.saveProveedorVendibleImage(file, proveedorId, vendibleName);
			return new ResponseEntity<String>(fileFullUrl, HttpStatus.OK);
		} catch (IOException | ImageUploadException e) {
			return new ExceptionFactory().getResponseException(
					"Error al cargar la imagen, por favor verifique que el archivo no esté dañado",
					HttpStatus.CONFLICT);
		}
	}

	@PostMapping(ImagenesControllerUrls.UPLOAD_PROVEEDOR_PHOTO_URL)
	public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file,
			@PathVariable("proveedorId") Long proveedorId) {
		try {
			String fileFullUrl = imageService.saveProveedorProfilePhoto(file, proveedorId);
			return new ResponseEntity<String>(fileFullUrl, HttpStatus.OK);
		} catch (IOException | ImageUploadException e) {
			return new ExceptionFactory().getResponseException(
					"Error al cargar la imagen, por favor verifique que el archivo no esté dañado",
					HttpStatus.CONFLICT);
		}
	}
	
	@GetMapping(ImagenesControllerUrls.IMAGE_BASE_URL)
	public ResponseEntity<?> proveedorImageExists(@RequestParam(name = "imagePath") String fileName) {
		boolean imageExists = imageService.imageIsStored(fileName);
		return imageExists ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
