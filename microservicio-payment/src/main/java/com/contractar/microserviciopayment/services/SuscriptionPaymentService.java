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
public class SuscriptionPaymentService{
	private SuscriptionPaymentRepository repository;
	
	public SuscriptionPaymentService(SuscriptionPaymentRepository repository) {
		this.repository = repository;
	}
	
	
	public boolean isSuscriptionValid(Long suscriptionId, OutsitePaymentProvider currentProviderImpl) {
		// TODO: handle non OUTSITE providers	
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
				|| (paymentMonth.equals(currentMonth.minus(1)) && payment.getDate().plusMonths(1).isAfter(LocalDate.now()));
			
		return wasPaymentDoneAtExpectedYear && 
				wasPaymentDoneAtExpectedMonth && 
				currentProviderImpl.wasPaymentAccepted(payment);	
	}
	
	public boolean canSuscriptionBePayed(Long suscriptionId, OutsitePaymentProvider currentProviderImpl) {
		Optional<SuscriptionPayment> lastPaymentOpt = repository.findTopBySuscripcionIdOrderByDateDesc(suscriptionId);
		
		if (lastPaymentOpt.isEmpty()) {
			return true;
		}
		
		SuscriptionPayment lastPayment = lastPaymentOpt.get();
		
		LocalDate suscriptionExpirationDate = lastPayment.getDate().plusMonths(1);
		
		LocalDate minimalPayDate = suscriptionExpirationDate.minusDays(10);
		
		LocalDate today = LocalDate.now();
		
		boolean isInsideExpectedDates = today.isEqual(minimalPayDate) || (today.isAfter(minimalPayDate) && today.isBefore(suscriptionExpirationDate));
		
		if (isInsideExpectedDates) {
			return true;
		}
		
		
		
	}
}
