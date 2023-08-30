package com.contractar.microserviciovendible.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.contractar.microserviciocommons.infra.CachedBodyHttpServletRequest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CachingRequestBodyFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		 HttpServletRequest currentRequest = (HttpServletRequest) request;
		CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(currentRequest);
        chain.doFilter(cachedBodyHttpServletRequest, response);

	}

}
