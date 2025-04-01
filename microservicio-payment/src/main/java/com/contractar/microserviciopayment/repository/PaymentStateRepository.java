package com.contractar.microserviciopayment.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.PaymentState;

@Repository
public interface PaymentStateRepository extends CrudRepository<PaymentState, Long> {
	public List<PaymentState> findAll();
}
