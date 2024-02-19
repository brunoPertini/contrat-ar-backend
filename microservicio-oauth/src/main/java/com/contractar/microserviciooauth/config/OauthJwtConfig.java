package com.contractar.microserviciooauth.config;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class OauthJwtConfig {

	@Value("classpath:contractar.jks")
	Resource resource;

	@Value("${security.keyStore.password}")
	private String keyStorePassword;

	@Value("${security.keyStore.keyAlias}")
	private String keyAlias;

	private KeyStore keyStore;
	

	private KeyStore loadKeyStore() {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

			InputStream inputStream = resource.getInputStream();

			if (inputStream == null) {
				throw new RuntimeException("Failed to locate the keystore file");
			}

			keyStore.load(inputStream, keyStorePassword.toCharArray());

			return keyStore;
		} catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
			throw new RuntimeException("Failed to load KeyStore", e);
		}
	}

	@Bean
	public RSAPublicKey storePublicKey() {
		try {
			keyStore = loadKeyStore();
			return (RSAPublicKey) keyStore.getCertificate(keyAlias).getPublicKey();
		} catch (KeyStoreException e) {
			throw new RuntimeException("Failed to retrieve public key from KeyStore", e);
		}
	}

	@Bean
	public RSAPrivateKey storePrivateKey() {
		try {
			keyStore = loadKeyStore();
			return (RSAPrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed to retrieve private key from KeyStore", e);
		}
	}
}
