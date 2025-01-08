package com.contractar.microserviciopayment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciopayment.models.PaymentProvider;

public interface PaymentProviderRepository extends CrudRepository<PaymentProvider, Long> {
	
	public Optional<PaymentProvider> findById(Long id);

	public List<PaymentProvider> findByIsActiveTrue();
}
