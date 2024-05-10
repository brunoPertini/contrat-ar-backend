package com.contractar.microserviciousuario.admin.services;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contractar.microserviciocommons.dto.usuario.UsuarioSensibleInfoDTO;
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

	public void addChangeRequestEntry(UsuarioSensibleInfoDTO newInfo, Long id)
			throws IllegalAccessException, ChangeAlreadyRequestedException {
		HashMap<String, Object> infoAsMap = (HashMap<String, Object>) ReflectionHelper.getObjectFields(newInfo);

		boolean someInfoAlreadyRequested = infoAsMap.keySet().stream().anyMatch(newInfoKey -> {
			Long matchingRequest = repository.getMatchingChangeRequest(id, newInfoKey);
			return matchingRequest != null;
		});
		if (someInfoAlreadyRequested) {
			throw new ChangeAlreadyRequestedException();
		}

		StringBuilder attributesBuilder = new StringBuilder("");

		infoAsMap.forEach((key, value) -> {
			if (value != null) {
				attributesBuilder.append(key).append("=")
				.append('\''+value.toString()+'\'')
				.append(",");
			}
		});

		attributesBuilder.deleteCharAt(attributesBuilder.length() - 1);

		ChangeRequest newRequest = new ChangeRequest("usuario", attributesBuilder.toString(), false, id);
		repository.save(newRequest);
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
