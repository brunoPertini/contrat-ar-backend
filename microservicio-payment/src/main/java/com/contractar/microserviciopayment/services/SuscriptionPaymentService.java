package com.contractar.microserviciopayment.services;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.contractar.microserviciopayment.models.SuscriptionPayment;
import com.contractar.microserviciopayment.providers.OutsitePaymentProvider;
import com.contractar.microserviciopayment.providers.uala.Uala;
import com.contractar.microserviciopayment.repository.OutsitePaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentProviderRepository;
import com.contractar.microserviciopayment.repository.PaymentRepository;
import com.contractar.microserviciopayment.repository.SuscriptionPaymentRepository;

@Service
public class SuscriptionPaymentService extends PaymentService{
	private SuscriptionPaymentRepository repository;
	
	public SuscriptionPaymentService(OutsitePaymentProviderRepository outsitePaymentProviderRepository, 
			PaymentProviderRepository paymentProviderRepository,
			PaymentRepository paymentRepository,
			Uala ualaPaymentProviderService,
			SuscriptionPaymentRepository suscriptionPaymentRepository,
			RestTemplate httpClient,
			SuscriptionPaymentRepository repository) {
		super(outsitePaymentProviderRepository, paymentProviderRepository, paymentRepository, ualaPaymentProviderService, suscriptionPaymentRepository, httpClient);
		this.repository = repository;
	}
	
	
	public boolean isSuscriptionValid(Long suscriptionId) {
		Optional<SuscriptionPayment> lastPaymentOpt = repository.findTopBySuscripcionIdOrderByDateDesc(suscriptionId);
		
		if (lastPaymentOpt.isEmpty()) {
			return false;
		}
		
		SuscriptionPayment payment = lastPaymentOpt.get();
		
		YearMonth paymentPeriod = payment.getPaymentPeriod();
		
		boolean wasPaymentDoneAtExpectedYear = paymentPeriod.getYear() == YearMonth.now().getYear();
		
		Month paymentMonth = paymentPeriod.getMonth();
		
		Month currentMonth = YearMonth.now().getMonth();
		
		boolean wasPaymentDoneAtExpectedMonth = paymentMonth.equals(currentMonth) 
				|| (paymentMonth.equals(currentMonth.minus(1)) && payment.getDate().plusMonths(1).isBefore(LocalDate.now()));
		
		// TODO: handle non OUTSITE providers
		
		OutsitePaymentProvider currentProviderImpl = this.createOutsitePaymentProvider(this.currentProvider.getId());
		
		return wasPaymentDoneAtExpectedYear && 
				wasPaymentDoneAtExpectedMonth && 
				currentProviderImpl.wasPaymentAccepted(payment);	
	}
}
