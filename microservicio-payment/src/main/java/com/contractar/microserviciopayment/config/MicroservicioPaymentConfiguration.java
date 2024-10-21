package com.contractar.microserviciopayment.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class MicroservicioPaymentConfiguration {

	private PaymentProviderRepository paymentProviderRepository;

	public MicroservicioPaymentConfiguration(PaymentProviderRepository paymentProviderRepository) {
		this.paymentProviderRepository = paymentProviderRepository;
	}

	private PaymentProvider createPaymentProvider() {
		List<PaymentProvider> paymentProviders = paymentProviderRepository.findByIsActiveTrue();
		if (paymentProviders.isEmpty()) {
			throw new IllegalStateException("No active payment provider found");
		}

		return paymentProviders.get(0);

	}

	@Bean
	RestTemplate httpClient() {
		return  new RestTemplate();
	}

	@Bean
	@Lazy
	PaymentProvider activeOutsitePaymentProvider() {

		return createPaymentProvider();

	}

	@PostConstruct
	public void postInit() {
		createPaymentProvider();
	}

}
