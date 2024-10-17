package com.contractar.microserviciopayment.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciopayment.models.PaymentProvider;

public interface PaymentProviderRepository extends CrudRepository<PaymentProvider, Long> {
	public List<PaymentProvider> findByIsActiveTrue();
}
