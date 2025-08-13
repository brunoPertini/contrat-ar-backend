package com.contractar.microservicioimagenes.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.contractar.microservicioimagenes.exceptions.ImageUploadException;

@Service
public class ImageService {

	@Autowired
	private RestTemplate httpClient;

	@Value("${cdn.location}")
	private String cdnBaseUrl;
	
	@Value("${cdn.publicUrl}")
	private String cdnPublicUrl;
	
	@Value("${SPRING_PROFILES_ACTIVE:dev}")
	private String currentProfile;

	private final String[] acceptedFormats = { "jpg", "jpeg", "png" };
	

	private String findImageType(String input) {
		int lastSlashIndex = input.lastIndexOf('/');

		if (lastSlashIndex != -1 && lastSlashIndex < input.length() - 1) {
			return input.substring(lastSlashIndex + 1);
		} else {
			return "";
		}
	}

	/**
	 * Crops image to aspect ratio 1:1
	 * 
	 * @param originalBytes
	 * @param imageFileType
	 * @return
	 * @throws IOException
	 */
	private byte[] cropToSquare(byte[] originalBytes, String imageFileType) throws IOException {
		ByteArrayInputStream originalInputStream = new ByteArrayInputStream(originalBytes);
		BufferedImage originalImage = ImageIO.read(originalInputStream);

		int targetSize = Math.min(originalImage.getWidth(), originalImage.getHeight());
		int startX = (originalImage.getWidth() - targetSize) / 2;
		int startY = (originalImage.getHeight() - targetSize) / 2;

		// Crea una nueva imagen cuadrada
		BufferedImage croppedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = croppedImage.createGraphics();

		g.drawImage(originalImage, 0, 0, targetSize, targetSize, startX, startY, startX + targetSize,
				startY + targetSize, null);
		g.dispose();

		ByteArrayOutputStream croppedOutputStream = new ByteArrayOutputStream();

		ImageIO.write(croppedImage, imageFileType, croppedOutputStream);

		return croppedOutputStream.toByteArray();
	}

	public String uploadProveedorVendibleImageToCDN(MultipartFile file, String uploadDirTemplate)
			throws IOException, ImageUploadException {

		byte[] bytes = file.getBytes();
		String imageFileType = findImageType(file.getContentType());

		boolean isAcceptedFormat = Arrays.stream(acceptedFormats).anyMatch(format -> format.equals(imageFileType));

		if (!isAcceptedFormat)
			throw new ImageUploadException("Formato no aceptado");

		byte[] croppedBytes = cropToSquare(bytes, imageFileType);

		String fullUploadUrl = cdnBaseUrl + "/upload/" + uploadDirTemplate;

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		ByteArrayResource resource = new ByteArrayResource(croppedBytes) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};

		LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", resource);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		
		try {
			ResponseEntity<String> response = httpClient.postForEntity(fullUploadUrl, requestEntity, String.class);
		} catch (HttpServerErrorException e) {
			throw new ImageUploadException("Upload failed: " + 409);
		}

		return cdnPublicUrl + "/" + uploadDirTemplate + "/" + file.getOriginalFilename();
	}

	/**
	 * 
	 * @param path image full url in the CDN
	 * @return if the image is stored in the system's CDN
	 */
	public boolean imageIsStored(String path) {
		try {
			String parsedPath = path;
			if (!currentProfile.equals("prod")) {
				parsedPath = path.replace("http://localhost:8000", cdnBaseUrl);
			}
			ResponseEntity<Void> response = httpClient.getForEntity(parsedPath, Void.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (Exception e) {
			return false;
		}

	}

	public String saveProveedorVendibleImage(MultipartFile file, Long proveedorId, String vendibleName)
			throws IOException, ImageUploadException {
		final String UPLOAD_DIR_TEMPLATE = "proveedores" + File.separator + proveedorId.toString() + File.separator
				+ vendibleName;

		return uploadProveedorVendibleImageToCDN(file, UPLOAD_DIR_TEMPLATE);

	}

	public String saveProveedorProfilePhoto(MultipartFile file, Long proveedorId)
			throws IOException, ImageUploadException {
		final String UPLOAD_DIR_TEMPLATE = "proveedores" + File.separator + proveedorId.toString();

		return uploadProveedorVendibleImageToCDN(file, UPLOAD_DIR_TEMPLATE);

	}

	public String saveTemporalProveedorProfilePhoto(MultipartFile file, String dni)
			throws IOException, ImageUploadException {
		final String UPLOAD_DIR_TEMPLATE = "proveedores" + File.separator + dni;

		return uploadProveedorVendibleImageToCDN(file, UPLOAD_DIR_TEMPLATE);

	}

}