package com.contractar.microserviciocommons.infra;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.contractar.microserviciocommons.dto.EntityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

public class RequestsHelper {
	
	/**
	 * Reads row content from request body (should be PUT, PATCH or POST) and parses it to an EntityDTO 
	 * @param request
	 * @param dtoClass
	 * @return
	 * @throws IOException
	 */
	public static EntityDTO parseRequestBodyToDTO(WebRequest request, Class<? extends EntityDTO> dtoClass) throws IOException {
		NativeWebRequest nativeWebRequest = (NativeWebRequest) request;

		HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);

		InputStream inputStream = httpServletRequest.getInputStream();
		byte[] body = StreamUtils.copyToByteArray(inputStream);

		String requestBody = new String(body);

		ObjectMapper objectMapper = new ObjectMapper();
		
		return  objectMapper.readValue(requestBody, dtoClass);
	}
}

