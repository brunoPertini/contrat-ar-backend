package com.contractar.microserviciopayment.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.OutsitePaymentProvider;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.models.enums.IntegrationType;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class MicroservicioPaymentConfiguration {

	private PaymentProviderRepository paymentProviderRepository;

	public MicroservicioPaymentConfiguration(PaymentProviderRepository paymentProviderRepository) {
		this.paymentProviderRepository = paymentProviderRepository;
	}

	private OutsitePaymentProvider createOutsitePaymentProvider() {
		List<PaymentProvider> paymentProviders = paymentProviderRepository.findByIsActiveTrue();
		if (paymentProviders.isEmpty()) {
			throw new IllegalStateException("No active payment provider found");
		}

		PaymentProvider activePaymentProvider = paymentProviders.get(0);

		if (!activePaymentProvider.getIntegrationType().equals(IntegrationType.OUTSITE)) {
			throw new IllegalStateException("Active payment provider is not of OUTSITE type");
		}

		if (activePaymentProvider instanceof OutsitePaymentProvider) {
			return (OutsitePaymentProvider) activePaymentProvider;
		} else {
			throw new IllegalStateException("Active payment provider is not an OutsitePaymentProvider");
		}
	}

	@Bean
	RestTemplate httpClient() {
		return new RestTemplate();
	}

	@Bean
	@Lazy
	OutsitePaymentProvider activeOutsitePaymentProvider() {

		return createOutsitePaymentProvider();

	}

	@PostConstruct
	public void postInit() {
		createOutsitePaymentProvider();
	}

}
