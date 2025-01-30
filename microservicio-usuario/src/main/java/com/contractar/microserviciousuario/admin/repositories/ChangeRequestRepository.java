package com.contractar.microserviciousuario.admin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.contractar.microserviciousuario.admin.models.ChangeRequest;

public interface ChangeRequestRepository extends CrudRepository<ChangeRequest, Long> {

	public List<ChangeRequest> findAll();

	public Optional<ChangeRequest> findById(Long id);

	@SuppressWarnings("unchecked")
	public ChangeRequest save(ChangeRequest request);
	
	public void deleteById(Long id);

	@Query(value = "SELECT cr.id FROM change_request cr WHERE (source_table_ids LIKE:sourceTableIds) AND NOT was_applied AND exists \n"
			+ "(SELECT id FROM contract_ar.change_request c WHERE c.id = cr.id AND c.attributes LIKE CONCAT('%', :searchAttribute, '%'))", nativeQuery = true)
	public Long getMatchingChangeRequest(@Param("sourceTableIds") String sourceTableIds,
			@Param("searchAttribute") String searchAttribute);
}
