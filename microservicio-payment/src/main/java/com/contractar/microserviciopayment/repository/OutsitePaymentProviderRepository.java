package com.contractar.microserviciopayment.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.OutsitePaymentProviderImpl;

@Repository
public interface OutsitePaymentProviderRepository extends CrudRepository<OutsitePaymentProviderImpl, Long> {
	@SuppressWarnings("unchecked")
	public OutsitePaymentProviderImpl save(OutsitePaymentProviderImpl p);
	
	public OutsitePaymentProviderImpl findByName(String name);
}
