package com.contractar.microserviciopayment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.UalaPaymentProvider;

@Repository
public interface UalaPaymentProviderRepository extends CrudRepository<UalaPaymentProvider, Long> {
	public UalaPaymentProvider save(UalaPaymentProvider p);
}
