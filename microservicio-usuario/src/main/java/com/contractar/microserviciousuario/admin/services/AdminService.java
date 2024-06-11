package com.contractar.microserviciousuario.admin.services;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.usuario.UsuarioDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.admin.dtos.UsuarioAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuariosByTypeResponse;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepository;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepositoryImpl;
import com.contractar.microserviciousuario.admin.repositories.ClienteAdminRepository;
import com.contractar.microserviciousuario.admin.repositories.ProveedorAdminRepository;
import com.contractar.microserviciousuario.helpers.DtoHelper;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;

@Service
public class AdminService {
	@Autowired
	private ChangeRequestRepository repository;

	@Autowired
	private ChangeRequestRepositoryImpl repositoryImpl;
	
	@Autowired
	private ProveedorAdminRepository proveedorAdminRepository;
	
	@Autowired
	private ClienteAdminRepository clienteAdminRepository;
	
	
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
				//TODO: This is to not add unnecessary ' when applying the UPDATE operation later. Refactor it
				String formattedValue = value instanceof String ? '\''+value.toString()+'\'' : value.toString();
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
	
	private Supplier<List<ProveedorDTO>> getProveedores() {
		return  () -> {
			return proveedorAdminRepository.findAll()
					.stream()
					.map(DtoHelper::toProveedorAdminDTO)
					.collect(Collectors.toList());
		};
	}
	
	private Supplier<List<UsuarioAdminDTO>> getClientes() {
			return () -> {
				return clienteAdminRepository.findAll()
						.stream()
						.map(DtoHelper::toUsuarioAdminDTO)
						.collect(Collectors.toList());
			};
	};
	
	public UsuariosByTypeResponse getAllUsuariosByType(@Nullable String usuarioType) {
		UsuariosByTypeResponse response = new UsuariosByTypeResponse();
				
		Optional.ofNullable(usuarioType).ifPresentOrElse((type) -> {
			if (type.equals("proveedores")) {
				response.getUsuarios().put("proveedores", getProveedores().get());
			} else {
				response.getUsuarios().put("clientes", getClientes().get());
			}
		}, () -> {
			response.getUsuarios().put("proveedores", getProveedores().get());
			response.getUsuarios().put("clientes", getClientes().get());
		});
		
		
		
		return response;
	}
	
	public UsuariosByTypeResponse getAllUsuariosByTypeAndNameOrSurname(@NonNull String usuarioType, String name, String surname) {
		UsuariosByTypeResponse response = new UsuariosByTypeResponse();
		boolean bothExist = name != null && surname != null;
		
		Supplier<List<? extends Usuario>> repositoryFunction = () -> {
			if (bothExist) {
				return usuarioType.equals("proveedores") ? proveedorAdminRepository.findAllByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name, surname) :
					clienteAdminRepository.findAllByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(name, surname);
			} else if (name != null) {
				return usuarioType.equals("proveedores") ? proveedorAdminRepository.findAllByNameContainingIgnoreCase(name) : 
					clienteAdminRepository.findAllByNameContainingIgnoreCase(name);
			} else {
				return usuarioType.equals("proveedores") ? proveedorAdminRepository.findAllBySurnameContainingIgnoreCase(name) : 
					clienteAdminRepository.findAllBySurnameContainingIgnoreCase(name);
			}
		};
		
		List<? extends Usuario> filteredUsuarios = repositoryFunction.get();
		
		
		
		if (usuarioType.equals("proveedores")) {
			response.getUsuarios().put("proveedores", filteredUsuarios
					.stream()
					.map(u -> {
						Proveedor p = (Proveedor) u;
						return new ProveedorDTO(p);
					})
					.collect(Collectors.toList()));
		} else {
			response.getUsuarios().put("clientes", filteredUsuarios
					.stream()
					.map(DtoHelper::toUsuarioDTO)
					.collect(Collectors.toList()));
		}
		
		
		
		return response;
	}
}
