package com.contractar.microserviciocommons.infra;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;

public class SecurityHelper {

	private RestTemplate httpClient;
	
	@Value("${microservicio-security.url}")
	private String microservicioSecurityUrl;

	public SecurityHelper(RestTemplate restTemplate) {
		this.httpClient = restTemplate;
	}
	
	public String fetchPublicKey() {
		final String url = microservicioSecurityUrl + SecurityControllerUrls.GET_PUBLIC_KEY;
	    final int maxRetries = 5;
	    final int delayMillis = 2000;

	    for (int i = 1; i <= maxRetries; i++) {
	        try {
	        	return this.httpClient.getForObject(url, String.class);
	        } catch (ResourceAccessException e) {
	            System.out.println("Intento " + i + " falló: " + e.getMessage());
	            if (i == maxRetries) {
	                throw new IllegalStateException("No se pudo obtener la public_key de OAuth luego de " + maxRetries + " intentos", e);
	            }
	            try {
	                Thread.sleep(delayMillis);
	            } catch (InterruptedException ie) {
	                Thread.currentThread().interrupt();
	                throw new IllegalStateException("El thread fue interrumpido durante el reintento", ie);
	            }
	        }
	    }

	    throw new IllegalStateException("No debería llegar acá.");
	}


	public boolean isResponseContentTypeValid(String url, String expectedContentType) {
		try {
			ResponseEntity<Void> response = httpClient.getForEntity(url, Void.class);
			MediaType responseMediaType = response.getHeaders().getContentType();
			boolean isResponseOK = response.getStatusCode().is2xxSuccessful();
			return isResponseOK && responseMediaType != null
					&& responseMediaType.toString().contains(expectedContentType);
		} catch (Exception e) {
			return false;
		}
	}
	
	public RSAPublicKey getRSAPublicKeyFromString(String key) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = key.replace("-----BEGIN PUBLIC KEY-----", "")
                                 .replace("-----END PUBLIC KEY-----", "")
                                 .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);

        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(spec);

        if (publicKey instanceof RSAPublicKey) {
            return (RSAPublicKey) publicKey;
        } else {
            throw new IllegalArgumentException("La clave proporcionada no es una clave pública RSA");
        }
    }
}
