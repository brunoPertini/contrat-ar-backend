package com.contractar.microserviciogateway.filters;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

// TODO: come back and apply a reliable strategy for securing microservices network
@Component
@Deprecated()
public class AddInternalTokenFilter extends ZuulFilter {
	
	@Value("${INTERNAL_KEY: xxx}")
	private String internalKey;
	
	@Override
	public boolean shouldFilter() {
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader("X-Internal-Token", "TU_CLAVE_SECRETA");
        return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

}
