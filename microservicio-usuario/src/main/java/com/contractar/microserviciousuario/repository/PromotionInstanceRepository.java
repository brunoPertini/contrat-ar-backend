package com.contractar.microserviciousuario.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.PromotionInstanceId;
import com.contractar.microserviciousuario.models.PromotionType;

public interface PromotionInstanceRepository extends CrudRepository<PromotionInstance, PromotionInstanceId> {
	@SuppressWarnings("unchecked")
	public PromotionInstance save(PromotionInstance save);

	public int countByPromotionType(PromotionType type);

	Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionId(Long promotionId, Long suscriptionId);

	Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateBefore(Long promotionId,
			Long suscriptionId, LocalDate fecha);

	Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateAfter(Long promotionId,
			Long suscriptionId, LocalDate fecha);

}
