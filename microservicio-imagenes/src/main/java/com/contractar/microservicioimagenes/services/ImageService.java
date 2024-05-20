package com.contractar.microservicioimagenes.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.contractar.microservicioimagenes.exceptions.ImageUploadException;

@Service
public class ImageService {
	
	@Autowired
	private RestTemplate httpClient;

	@Value("${cdn.location.dev}")
	private String cdnBaseUrl;

	@Value("${cdn.dir.dev}")
	private String cdnDir;

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

	private void saveImageToFile(byte[] imageBytes, String fileName, String uploadDir) throws IOException {
		File directory = new File(uploadDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		String filePath = uploadDir + File.separator + fileName;

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
			Files.copy(inputStream, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

	}
	
	private String saveImageFile(MultipartFile file, String uploadDirTemplate) throws IOException, ImageUploadException {
		byte[] bytes = file.getBytes();

		String imageFileType = findImageType(file.getContentType());

		boolean isAcceptedFormat = Arrays.stream(acceptedFormats).anyMatch(format -> format.equals(imageFileType));

		if (!isAcceptedFormat) {
			throw new ImageUploadException("");
		}

		byte[] croppedBytes = cropToSquare(bytes, imageFileType);

		String fileName = file.getOriginalFilename();

		saveImageToFile(croppedBytes, fileName, cdnDir + File.separator + uploadDirTemplate);

		return cdnBaseUrl + File.separator + uploadDirTemplate + File.separator + fileName;
	}
	
	/**
	 * 
	 * @param path image full url in the CDN
	 * @return if the image is stored in the system's CDN
	 */
	public boolean imageIsStored(String path) {
		try {
			ResponseEntity<Void> response = httpClient.getForEntity(path, Void.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch(Exception e) {
			return false;
		}
		
	}
	

	public String saveProveedorVendibleImage(MultipartFile file, Long proveedorId, String vendibleName)
			throws IOException, ImageUploadException {
		final String UPLOAD_DIR_TEMPLATE = "proveedores" + File.separator + proveedorId.toString() + File.separator
				+ vendibleName;

		return saveImageFile(file, UPLOAD_DIR_TEMPLATE);

	}
	
	public String saveProveedorProfilePhoto(MultipartFile file, Long proveedorId)
			throws IOException, ImageUploadException {
		final String UPLOAD_DIR_TEMPLATE = "proveedores" + File.separator + proveedorId.toString();

		return saveImageFile(file, UPLOAD_DIR_TEMPLATE);

	}

}
