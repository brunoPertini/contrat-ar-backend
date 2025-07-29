package com.contractar.microserviciousuario.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.contractar.microservicioadapter.enums.PromotionType;
import com.contractar.microserviciousuario.models.PromotionInstance;
import com.contractar.microserviciousuario.models.PromotionInstanceId;

public interface PromotionInstanceRepository extends CrudRepository<PromotionInstance, PromotionInstanceId> {
	@SuppressWarnings("unchecked")
	public PromotionInstance save(PromotionInstance save);

	public int countByPromotionType(PromotionType type);

	
	@Query("""
		    SELECT pi FROM PromotionInstance pi
		    WHERE pi.id.promotionId = :promotionId
		      AND pi.subscription.usuario.id = :proveedorId
		""")
		Optional<PromotionInstance> findByPromotionIdAndProveedorId(
		    @Param("promotionId") Long promotionId,
		    @Param("proveedorId") Long proveedorId
		);
	
	@Query("""
		    SELECT pi FROM PromotionInstance pi
		      WHERE pi.id.suscriptionId = :suscriptionId
		      AND (pi.expirationDate > :fecha OR pi.expirationDate IS NULL)
		""")
	Optional<PromotionInstance> findByIdSuscriptionIdAndExpirationDateAfter(Long suscriptionId, LocalDate fecha);

	Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionId(Long promotionId, Long suscriptionId);

	Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateBefore(Long promotionId,
			Long suscriptionId, LocalDate fecha);

	@Query("""
		    SELECT pi FROM PromotionInstance pi
		    WHERE pi.id.promotionId = :promotionId
		      AND pi.id.suscriptionId = :suscriptionId
		      AND (pi.expirationDate > :fecha OR pi.expirationDate IS NULL)
		""")
		Optional<PromotionInstance> findByIdPromotionIdAndIdSuscriptionIdAndExpirationDateAfter(
		    @Param("promotionId") Long promotionId,
		    @Param("suscriptionId") Long suscriptionId,
		    @Param("fecha") LocalDate fecha);

}
