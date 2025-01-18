package com.contractar.microserviciopayment.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.SuscriptionPayment;

@Repository
public interface SuscriptionPaymentRepository extends CrudRepository<SuscriptionPayment, Long> {

	@SuppressWarnings("unchecked")
	SuscriptionPayment save(SuscriptionPayment s);

	Optional<SuscriptionPayment> findTopBySuscripcionIdOrderByDateDesc(Long suscripcionId);

}
