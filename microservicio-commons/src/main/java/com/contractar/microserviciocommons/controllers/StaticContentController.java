package com.contractar.microserviciocommons.controllers;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contractar.microserviciocommons.constants.controllers.StaticContentControllerUrls;
import com.contractar.microserviciocommons.services.GoogleCloudClient;

@RestController
public class StaticContentController {
	private GoogleCloudClient googleServicesClient;
	
	public StaticContentController(GoogleCloudClient googleServicesClient) {
		this.googleServicesClient = googleServicesClient;
	}
	
	@GetMapping(StaticContentControllerUrls.READ_CONTENT_AS_HTML)
	public ResponseEntity<String> getDocAsHtml(@PathVariable("id") String documentId) throws IOException, GeneralSecurityException {
		 String html = googleServicesClient.getDocumentAsHtml(documentId);
         return ResponseEntity.ok()
                 .header("Content-Type", "text/html")
                 .body(html);
	}
}
