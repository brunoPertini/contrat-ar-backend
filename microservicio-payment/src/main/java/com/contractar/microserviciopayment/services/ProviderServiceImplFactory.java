package com.contractar.microserviciopayment.services;

import org.springframework.stereotype.Component;

import com.contractar.microserviciopayment.providers.uala.Uala;

@Component
public  class ProviderServiceImplFactory {
	
	private Uala ualaPaymentProviderService;
	
	public ProviderServiceImplFactory(Uala ualaPaymentProviderService) {
		this.ualaPaymentProviderService = ualaPaymentProviderService;
	}
	
	public com.contractar.microserviciopayment.providers.OutsitePaymentProvider getOutsitePaymentProvider() {
		return ualaPaymentProviderService;
	}
}
