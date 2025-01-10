package com.contractar.microserviciopayment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.Payment;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {
	@SuppressWarnings("unchecked")
	public Payment save(Payment p);	
	
	public Optional<Payment> findById(Long id);
}
