package com.contractar.microserviciocommons.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.springframework.stereotype.Service;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.Drive;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;


@Service
public class GoogleCloudClient {
	private static final String APPLICATION_NAME = "ContractAr-Backend";
 
	public String getDocumentAsHtml(String documentId) throws IOException, GeneralSecurityException {
		String credentialJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
		InputStream stream = new ByteArrayInputStream(credentialJson.getBytes(StandardCharsets.UTF_8));

		GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/drive")); 

		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Drive driveService = new Drive.Builder(httpTransport, JacksonFactory.getDefaultInstance(), new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        driveService.files().export(documentId, "text/html")
                .executeMediaAndDownloadTo(outputStream);

        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
