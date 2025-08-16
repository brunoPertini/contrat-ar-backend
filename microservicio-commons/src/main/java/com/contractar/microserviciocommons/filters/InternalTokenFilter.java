package com.contractar.microserviciocommons.filters;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InternalTokenFilter extends OncePerRequestFilter {

	private final String expectedToken;

	public InternalTokenFilter(String expectedToken) {
		this.expectedToken = expectedToken;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = request.getHeader("X-Internal-Token");

		if (!expectedToken.equals(token)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
			return;
		}

		filterChain.doFilter(request, response);

	}
}
