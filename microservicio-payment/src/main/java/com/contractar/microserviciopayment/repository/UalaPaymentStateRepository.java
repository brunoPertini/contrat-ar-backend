package com.contractar.microserviciopayment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.UalaPaymentState;
import com.contractar.microserviciopayment.models.enums.UalaPaymentStateValue;

@Repository
public interface UalaPaymentStateRepository extends CrudRepository<UalaPaymentState, Long>{
	public Optional<UalaPaymentState> findByState(String state);
}
