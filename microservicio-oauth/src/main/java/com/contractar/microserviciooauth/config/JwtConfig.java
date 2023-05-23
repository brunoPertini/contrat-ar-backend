package com.contractar.microserviciooauth.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

	private String keyStorePath = "contractar-jwt.jks";

	private String keyStorePassword = "contractar";

	private String keyAlias = "contractar-oauth";

	private String privateKeyPassphrase = "contractar";

	private static Logger logger = LoggerFactory.getLogger(JwtConfig.class);
	
	private final KeyStore keyStore;
	
	@Autowired
    public JwtConfig() {
        this.keyStore = loadKeyStore();
    }

	 private KeyStore loadKeyStore() {
	  try {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // Obtener el ClassLoader del sistema
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Obtener la URL del archivo utilizando el ClassLoader
        URL url = classLoader.getResource(keyStorePath);
        if (url == null) {
            throw new RuntimeException("Failed to locate the keystore file");
        }

        // Cargar el archivo de keystore utilizando la URL
        try (InputStream inputStream = url.openStream()) {
            keyStore.load(inputStream, keyStorePassword.toCharArray());
        }

        return keyStore;
	    } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
	        throw new RuntimeException("Failed to load KeyStore", e);
	    }
    }

	@Bean
    public RSAPublicKey storePublicKey() {
        try {
            return (RSAPublicKey) keyStore.getCertificate(keyAlias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to retrieve public key from KeyStore", e);
        }
    }
	
	@Bean
	public RSAPrivateKey storePrivateKey() {
		try {
			return (RSAPrivateKey) keyStore.getKey(keyAlias, keyStorePassword.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			 throw new RuntimeException("Failed to retrieve private key from KeyStore", e);
		}
    }
}
