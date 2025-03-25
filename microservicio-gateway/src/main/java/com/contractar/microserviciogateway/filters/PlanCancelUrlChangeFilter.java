package com.contractar.microserviciogateway.filters;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpMethod;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class PlanCancelUrlChangeFilter extends ZuulFilter {

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		return request.getRequestURI().matches("^/plan/\\\\d+$") && request.getMethod().equals(HttpMethod.DELETE.name());
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		String requestURI = request.getRequestURI();
		String idChangeRequest = requestURI.replace("/plan/", "");

		// TODO: differentiate between dev and prod
		String newUrl = "http://localhost:8002/admin/change-requests/" + idChangeRequest + "/plan";

		ctx.put("requestURI", newUrl);
		ctx.set("requestURI", newUrl);

		return null;
	}

}
