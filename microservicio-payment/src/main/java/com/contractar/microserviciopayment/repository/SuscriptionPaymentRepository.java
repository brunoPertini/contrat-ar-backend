package com.contractar.microserviciopayment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.contractar.microserviciopayment.models.PaymentState;
import com.contractar.microserviciopayment.models.SuscriptionPayment;

@Repository
public interface SuscriptionPaymentRepository extends CrudRepository<SuscriptionPayment, Long> {

	@SuppressWarnings("unchecked")
	SuscriptionPayment save(SuscriptionPayment s);

	List<SuscriptionPayment> findAllBySuscripcionId(Long suscripcionId);
	
	@Query("SELECT s FROM SuscriptionPayment s WHERE (s.suscripcion IS NOT NULL AND s.suscripcion.usuario.id =:userId) OR (s.toBeBindUserId =:userId)")
	List<SuscriptionPayment> findAllBySuscripcionUsuarioProveedorId(Long userId);

	Optional<SuscriptionPayment> findTopBySuscripcionIdOrderByPaymentPeriodDesc(Long suscripcionId);

	Optional<SuscriptionPayment> findTopBySuscripcionIdAndStateOrderByPaymentPeriodDesc(Long suscripcionId,
			PaymentState state);

}
