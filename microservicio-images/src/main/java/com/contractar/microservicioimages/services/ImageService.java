package com.contractar.microservicioimages.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

	@Value("${cdn.location.dev}")
	private String cdnBaseUrl;

	private String findImageType(String input) {
		int indiceUltimoSlash = input.lastIndexOf('/');

		if (indiceUltimoSlash != -1 && indiceUltimoSlash < input.length() - 1) {
			return input.substring(indiceUltimoSlash + 1);
		} else {
			return "";
		}
	}

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
				
		ImageIO.write(croppedImage, "jpeg", croppedOutputStream);

		return croppedOutputStream.toByteArray();
	}

	private String saveImageToFile(byte[] imageBytes, String fileName, String uploadDir) throws IOException {
		File directory = new File(uploadDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		String filePath = uploadDir + File.separator + fileName;

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
			Files.copy(inputStream, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

		return filePath;
	}

	public String saveProveedorVendibleImage(MultipartFile file, Long proveedorId, String vendibleName) {
		if (!file.isEmpty()) {
			try {
				final String UPLOAD_DIR = cdnBaseUrl + File.separator + "proveedores" + File.separator
						+ proveedorId.toString() + File.separator + vendibleName;
				byte[] bytes = file.getBytes();
				
				String imageFileType = findImageType(file.getContentType());

				byte[] croppedBytes = cropToSquare(bytes, imageFileType);

				String filePath = saveImageToFile(croppedBytes, file.getOriginalFilename(), UPLOAD_DIR);

				return filePath;

			} catch (Exception e) {
				return "Error al procesar la imagen.";
			}
		} else {
			return "Por favor, selecciona un archivo.";
		}
	}

}
