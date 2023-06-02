package com.contractar.microserviciogateway.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

@Order(1)
@Configuration
public class JwtConfig {
	
	@Value("classpath:contractar-jwt.jks")
	Resource resource;
	
	private String keyStorePassword = "contractar";

	private String keyAlias = "contractar-oauth";

	private String privateKeyPassphrase = "contractar";

	private static Logger logger = LoggerFactory.getLogger(JwtConfig.class);

	private KeyStore keyStore;
	
	private Algorithm algorithm;
	private JWTVerifier verifier;
	  

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
