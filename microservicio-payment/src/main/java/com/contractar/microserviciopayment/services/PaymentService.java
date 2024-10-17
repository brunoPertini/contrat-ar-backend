package com.contractar.microserviciopayment.services;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.contractar.microserviciopayment.models.OutsitePaymentProvider;
import com.contractar.microserviciopayment.models.PaymentProvider;
import com.contractar.microserviciopayment.providers.uala.Uala;

@Service
public class PaymentService {
	
	private OutsitePaymentProvider activePaymentProvider;
	
	private Uala ualaPaymentProviderService;

	public PaymentService(OutsitePaymentProvider activePaymentProvider, Uala ualaPaymentProviderService) {
		this.activePaymentProvider = activePaymentProvider;
		this.ualaPaymentProviderService = ualaPaymentProviderService;
	}
	
	private OutsitePaymentProvider createOutsitePaymentProvider(PaymentProvider provider) {
		Map<Integer, com.contractar.microserviciopayment.providers.OutsitePaymentProvider> creators = Map.of(1,  ualaPaymentProviderService);
	}
	
	/**
	 * 
	 * @param suscriptionId
	 * @param amount
	 * @return The checkout url to be used by the frontend so the user can finish the pay there
	 */
	public String payLastSuscriptionPeriod(Long suscriptionId, int amount) {
		Optional<String> tokenOpt = Optional.ofNullable(activePaymentProvider.getToken());
		
		if (!tokenOpt.isPresent() ||  !StringUtils.hasLength(tokenOpt.get())) {
			
		}
	}

}
