package com.contractar.microserviciopayment.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.OutsitePaymentProvider;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;

@Configuration
public class MicroservicioPaymentConfiguration {
	
	private PaymentProviderRepository paymentProviderRepository;
	
	public MicroservicioPaymentConfiguration(PaymentProviderRepository paymentProviderRepository) {
		this.paymentProviderRepository = paymentProviderRepository;
	}

	@Bean
	RestTemplate httpClient() {
		return new RestTemplate();
	}
	
	@Bean
	OutsitePaymentProvider activeOutsitePaymentProvider() {
		List<PaymentProvider> paymentProviders = paymentProviderRepository.findByIsActiveTrue();
		if(paymentProviders.isEmpty()) {
			// TODO: disparar excepcion
			return null;
		}
		
		PaymentProvider activePaymentProvider = paymentProviders.get(0);
		
		if(!activePaymentProvider.getIntegrationType().equals(IntegrationType.OUTSITE)) {
			// TODO: disparar excepcion
			return null;
		}
		
		return (OutsitePaymentProvider) activePaymentProvider;

	}

}
