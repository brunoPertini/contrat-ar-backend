package com.contractar.microserviciousuario.admin.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepository;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepositoryImpl;

@Service
public class AdminService {
	@Autowired
	private ChangeRequestRepository repository;

	@Autowired
	private ChangeRequestRepositoryImpl repositoryImpl;
	
	
	public boolean requestExists(Long sourceTableId, List<String> attributes) {
		return !attributes.isEmpty() &&
				repositoryImpl.getMatchingChangeRequest(sourceTableId, attributes) != null;
	}

	public void addChangeRequestEntry(UsuarioSensibleInfoDTO newInfo, Long sourceTableId)
			throws IllegalAccessException, ChangeAlreadyRequestedException {
		HashMap<String, Object> infoAsMap = (HashMap<String, Object>) ReflectionHelper.getObjectFields(newInfo);

		boolean someInfoAlreadyRequested = infoAsMap.keySet().stream().anyMatch(newInfoKey -> {
			Long matchingRequest = repository.getMatchingChangeRequest(sourceTableId, newInfoKey);
			return matchingRequest != null;
		});

		if (someInfoAlreadyRequested) {
			throw new ChangeAlreadyRequestedException();
		}

		StringBuilder attributesBuilder = new StringBuilder("");

		infoAsMap.forEach((key, value) -> {
			if (value != null) {
				String formattedValue = value instanceof String ? '\''+value.toString()+'\'' : value.toString(); //This is to not add unnecessary ' when applying the UPDATE operation later. TODO: refactor it.
				attributesBuilder.append(key).append("=")
				.append(formattedValue)
				.append(",");
			}
		});

		if (!attributesBuilder.isEmpty()) {
			attributesBuilder.deleteCharAt(attributesBuilder.length() - 1);
			ChangeRequest newRequest = new ChangeRequest("usuario", attributesBuilder.toString(), false, sourceTableId, "id");
			repository.save(newRequest);
		}
		
	}
	
	public void addChangeRequestEntry(PlanType plan, Long proveedorId) throws ChangeAlreadyRequestedException {
		boolean infoAlreadyRequested = requestExists(proveedorId, List.of(plan.toString()));
		
		if (infoAlreadyRequested) {
			throw new ChangeAlreadyRequestedException();
		}
		
		String planAttributeChangeQuery = "plan="+"\'"+plan.toString()+"\'";
		
		ChangeRequest planChangeRequest = new ChangeRequest("proveedor", planAttributeChangeQuery, false, proveedorId, "proveedor_id");
		repository.save(planChangeRequest);

	}

	public void confirmChangeRequest(Long id) throws ChangeConfirmException {
		Optional<ChangeRequest> requestOpt = repository.findById(id);

		if (requestOpt.isEmpty()) {
			throw new ChangeConfirmException();
		}

		ChangeRequest change = requestOpt.get();

		repositoryImpl.applyChangeRequest(change);
	}
}
