package com.contractar.microserviciousuario.admin.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.services.ChangeConfirmException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class ChangeRequestRepositoryImpl {
	@PersistenceContext
	private EntityManager entityManager;

	public void applyChangeRequest(ChangeRequest changeRequest) throws ChangeConfirmException {
		StringBuilder queryBuilder = new StringBuilder("UPDATE ").append(changeRequest.getSourceTable()).append(" SET ")
				.append(changeRequest.getAttributes());
		
		List<String> sourceTableIdNames = changeRequest.getSourceTableIdNames();
		List<Long> sourceTableIds = changeRequest.getSourceTableIds();
		
		int i = 0;
		
		queryBuilder.append(" WHERE ("+sourceTableIdNames.get(i)+"=" + sourceTableIds.get(i) + ")");
		
		i += 1;
		
		while(i< sourceTableIdNames.size()) {
			queryBuilder.append(" AND ("+sourceTableIdNames.get(i)+"=" + sourceTableIds.get(i) + ")");
			i+=1;
		}

		try {
			int updatedCount = entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
			if (updatedCount == 0) {
				throw new ChangeConfirmException();
			}

			changeRequest.setWasApplied(true);
			
			entityManager.persist(changeRequest);
			
		} catch (Exception e) {
			throw new ChangeConfirmException();
		}

	}
	
	 public Long getMatchingChangeRequest(Long sourceTableId, List<String> searchAttributes) {
	        StringBuilder queryBuilder = new StringBuilder(
	            "SELECT cr.id FROM change_request cr WHERE (source_table_id = :sourceTableId) AND NOT was_applied");

	        for (int i = 0; i < searchAttributes.size(); i++) {
	            queryBuilder.append(" AND EXISTS (SELECT id FROM change_request c WHERE c.id = cr.id AND c.attributes LIKE :searchAttribute")
	                        .append(i)
	                        .append(")");
	        }

	        Query query = entityManager.createNativeQuery(queryBuilder.toString());
	        query.setParameter("sourceTableId", sourceTableId);

	        for (int i = 0; i < searchAttributes.size(); i++) {
	            query.setParameter("searchAttribute" + i, "%" + searchAttributes.get(i) + "%");
	        }

	        try {
	        	return (Long) query.getSingleResult();
	        } catch (Exception e) {
	        	return null;
	        }
	    }

}
