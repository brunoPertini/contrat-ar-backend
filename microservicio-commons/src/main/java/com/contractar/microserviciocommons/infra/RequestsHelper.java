package com.contractar.microserviciocommons.infra;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

public class RequestsHelper {
	
	private RequestsHelper() {}
	
	/**
	 * Reads raw content from request body (should be PUT, PATCH or POST) and parses it to a VendibleDTO 
	 * @param request
	 * @param dtoClass
	 * @return
	 * @throws IOException
	 */
	public static VendibleDTO parseRequestBodyToDTO(WebRequest request, Class<? extends VendibleDTO> dtoClass) throws IOException {
		NativeWebRequest nativeWebRequest = (NativeWebRequest) request;

		HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

		InputStream inputStream = httpServletRequest.getInputStream();
		byte[] body = StreamUtils.copyToByteArray(inputStream);

		String requestBody = new String(body);

		ObjectMapper objectMapper = new ObjectMapper();
		
		return  objectMapper.readValue(requestBody, dtoClass);
	}
	
	public static boolean isImageUrl(URL url) {
	    try (InputStream inputStream = url.openStream()) {
	        byte[] header = new byte[8];
	        int read = inputStream.read(header);

	        if (read < 8) return false;

	        // PNG
	        if (header[0] == (byte) 0x89 && header[1] == 0x50 && header[2] == 0x4E &&
	            header[3] == 0x47 && header[4] == 0x0D && header[5] == 0x0A &&
	            header[6] == 0x1A && header[7] == 0x0A) {
	            return true;
	        }

	        // JPEG
	        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
	            return true;
	        }

	        // GIF
	        if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F') {
	            return true;
	        }

	        // WEBP (RIFF WEBP)
	        if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[8] == 'W') {
	            return true;
	        }

	        return false;
	    } catch (Exception e) {
	        return false;
	    }
	}
}

