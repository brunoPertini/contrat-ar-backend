package com.contractar.microservicioimages.controllers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageController {

	private static final String UPLOAD_DIR = "/home/bruno/Escritorio";

	@PostMapping("/image/upload")
	public String handleVendibleImageUpload(@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				byte[] croppedBytes = cropToSquare(bytes);

				String fileName = "vendible_" + file.getOriginalFilename();
				String filePath = saveImageToFile(croppedBytes, fileName, UPLOAD_DIR);

				return filePath;

			} catch (Exception e) {
				return "Error al procesar la imagen.";
			}
		} else {
			return "Por favor, selecciona un archivo.";
		}
	}

	private byte[] cropToSquare(byte[] originalBytes) throws IOException {
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
		ImageIO.write(croppedImage, "png", croppedOutputStream);

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

}
