package com.contractar.microserviciomailing.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public final class FileReader {

	public String readFile(String filePath) throws IOException {
		InputStream inputStream = getClass().getResourceAsStream(filePath);
		if (inputStream == null) {
			throw new IOException("Archivo no encontrado: " + filePath);
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			return reader.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}
}
