package com.contractar.microserviciousuario.admin.services;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microserviciocommons.dto.UsuarioFiltersDTO;
import com.contractar.microserviciocommons.dto.usuario.sensibleinfo.UsuarioSensibleInfoDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciousuario.admin.controllers.AdminController.UsuariosTypeFilter;
import com.contractar.microserviciousuario.admin.dtos.ProveedorAdminDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuarioPersonalDataUpdateDTO;
import com.contractar.microserviciousuario.admin.dtos.UsuariosByTypeResponse;
import com.contractar.microserviciousuario.admin.models.ChangeRequest;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepository;
import com.contractar.microserviciousuario.admin.repositories.ChangeRequestRepositoryImpl;
import com.contractar.microserviciousuario.admin.repositories.UsuarioAdminCustomRepository;
import com.contractar.microserviciousuario.helpers.DtoHelper;
import com.contractar.microserviciousuario.models.Cliente;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.Usuario;
import com.contractar.microserviciousuario.repository.ClienteRepository;
import com.contractar.microserviciousuario.repository.ProveedorRepository;

@Service
public class AdminService {
	@Autowired
	private ChangeRequestRepository repository;

	@Autowired
	private ChangeRequestRepositoryImpl repositoryImpl;

	@Autowired
	private UsuarioAdminCustomRepository usuarioAdminCustomRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private ProveedorRepository proveedorRepository;

	public boolean requestExists(Long sourceTableId, List<String> attributes) {
		return !attributes.isEmpty() && repositoryImpl.getMatchingChangeRequest(sourceTableId, attributes) != null;
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
				// TODO: This is to not add unnecessary ' when applying the UPDATE operation
				// later. Refactor it
				String formattedValue = value instanceof String ? '\'' + value.toString() + '\'' : value.toString();
				attributesBuilder.append(key).append("=").append(formattedValue).append(",");
			}
		});

		if (!attributesBuilder.isEmpty()) {
			attributesBuilder.deleteCharAt(attributesBuilder.length() - 1);
			ChangeRequest newRequest = new ChangeRequest("usuario", attributesBuilder.toString(), false, sourceTableId,
					"id");
			repository.save(newRequest);
		}

	}

	public void addChangeRequestEntry(PlanType plan, Long proveedorId) throws ChangeAlreadyRequestedException {
		boolean infoAlreadyRequested = requestExists(proveedorId, List.of(plan.toString()));

		if (infoAlreadyRequested) {
			throw new ChangeAlreadyRequestedException();
		}

		String planAttributeChangeQuery = "plan=" + "\'" + plan.toString() + "\'";

		ChangeRequest planChangeRequest = new ChangeRequest("proveedor", planAttributeChangeQuery, false, proveedorId,
				"proveedor_id");
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

	public UsuariosByTypeResponse getAllFilteredUsuarios(@NonNull String usuarioType, UsuarioFiltersDTO filters,
			@Nullable Boolean showOnlyActives, Long planId) throws IllegalAccessException {
		UsuariosByTypeResponse response = new UsuariosByTypeResponse();

		List<? extends Usuario> filteredUsuarios = usuarioAdminCustomRepository.getFilteredUsuarios(usuarioType,
				filters, showOnlyActives, planId);

		if (usuarioType.equals("proveedores")) {
			response.getUsuarios().put("proveedores", filteredUsuarios.stream()
					.map(u -> new ProveedorAdminDTO((Proveedor) u)).collect(Collectors.toList()));
		} else {
			response.getUsuarios().put("clientes",
					filteredUsuarios.stream().map(DtoHelper::toUsuarioAdminDTO).collect(Collectors.toList()));
		}

		return response;
	}

	public void updateUsuarioPersonalData(Long userId, UsuarioPersonalDataUpdateDTO newInfo,
			UsuariosTypeFilter usuarioType)
			throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		final Map<String, Function<Long, ? extends Usuario>> fetchEntity = Map.of(UsuariosTypeFilter.clientes.name(),
				(clienteId) -> {
					try {
						return clienteRepository.findById(clienteId).map(cliente -> cliente)
								.orElseThrow(() -> new UserNotFoundException());
					} catch (UserNotFoundException e) {
						throw new RuntimeException(e);
					}

				}, UsuariosTypeFilter.proveedores.name(), (proveedorId) -> {
					try {
						return proveedorRepository.findById(proveedorId).map(proveedor -> proveedor)
								.orElseThrow(() -> new UserNotFoundException());
					} catch (UserNotFoundException e) {
						throw new RuntimeException(e);
					}

				});

		Usuario usuarioEntity = fetchEntity.get(usuarioType.name()).apply(userId);
		String entityClassFullName = ReflectionHelper.getObjectClassFullName(usuarioEntity);
		
		if (usuarioType.equals(UsuariosTypeFilter.proveedores)) {
			ProveedorPersonalDataUpdateDTO  concreteProveedorDTO = (ProveedorPersonalDataUpdateDTO) newInfo;
			String proveedorDtoClassFullName = ReflectionHelper.getObjectClassFullName(concreteProveedorDTO);
			ReflectionHelper.applySetterFromExistingFields(concreteProveedorDTO, usuarioEntity, proveedorDtoClassFullName, entityClassFullName);
			proveedorRepository.save((Proveedor) usuarioEntity);
			
		} else {
			String proveedorDtoClassFullName = ReflectionHelper.getObjectClassFullName(newInfo);
			ReflectionHelper.applySetterFromExistingFields(newInfo, usuarioEntity, proveedorDtoClassFullName, entityClassFullName);
			clienteRepository.save((Cliente) usuarioEntity);
		}
		
	}
}
