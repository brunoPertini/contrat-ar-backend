package com.contractar.microserviciovendible.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microservicioadapter.entities.ProveedorAccessor;
import com.contractar.microservicioadapter.entities.ProveedorVendibleAccesor;
import com.contractar.microservicioadapter.entities.VendibleCategoryAccesor;
import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.constants.RolesNames;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.CategorizableObject;
import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.CouldntChangeStateException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.helpers.StringHelper;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.admin.dtos.ProveedorVendibleAdminDTO;
import com.contractar.microserviciousuario.models.Vendible;
import com.contractar.microserviciousuario.models.VendibleCategory;
import com.contractar.microserviciovendible.repository.ProductoRepository;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.repository.VendibleCategoryRepository;
import com.contractar.microserviciovendible.repository.VendibleRepository;
import com.contractar.microserviciovendible.services.resolvers.VendibleFetchingMethodResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class VendibleService {
	@Autowired
	private VendibleRepository vendibleRepository;

	@Autowired
	private ServicioRepository servicioRepository;

	@Autowired
	private ProductoRepository productoRepository;

	@Autowired
	private VendibleCategoryRepository vendibleCategoryRepository;

	@Value("${microservicio-usuario.url}")
	private String microServicioUsuarioUrl;

	@Value("${microservicio-security.url}")
	private String SERVICIO_SECURITY_URL;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SecurityHelper securityHelper;

	private VendibleCategory categoryParentAux;

	private static final int CATEGORY_BOUND = 3;

	private Object readJwtPayload(HttpServletRequest request) {
		String getPayloadUrl = SERVICIO_SECURITY_URL + SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> getPayloadResponse = restTemplate.exchange(getPayloadUrl, HttpMethod.GET, entity,
				Object.class);

		return getPayloadResponse.getBody();
	}

	@Transactional
	private VendibleCategory persistCategoryHierachy(VendibleCategory baseCategory) throws CantCreateException {
		ArrayList<VendibleCategory> hierachy = new ArrayList<VendibleCategory>();
		hierachy.add(baseCategory);

		Optional<VendibleCategory> parentOpt = Optional.ofNullable(baseCategory.getParent());
		while (parentOpt.isPresent()) {
			VendibleCategory parent = parentOpt.get();
			hierachy.add(parent);
			parentOpt = Optional.ofNullable(parent.getParent());
		}

		if (hierachy.size() > CATEGORY_BOUND) {
			throw new CantCreateException();
		}

		// Reversing so later can be persisted in an order that respect the database
		// constraints
		Collections.reverse(hierachy);

		String baseCategoryName = baseCategory.getName();

		int firstParentIndex = -1;
		int secondParentIndex = -1;

		if (hierachy.size() == 3) {
			firstParentIndex = 1;
			secondParentIndex = 0;
		}

		if (hierachy.size() == 2) {
			firstParentIndex = 0;
			secondParentIndex = -1;
		}

		String firstParentName = (firstParentIndex != -1 && firstParentIndex < hierachy.size())
				? hierachy.get(firstParentIndex).getName()
				: null;
		String secondParentName = (secondParentIndex != -1 && secondParentIndex < hierachy.size())
				? hierachy.get(secondParentIndex).getName()
				: null;

		boolean shouldNotCheckForParents = firstParentName == null && secondParentName == null;

		if (shouldNotCheckForParents) {
			VendibleCategory isolatedCategory = vendibleCategoryRepository.findAloneCategory(baseCategoryName);
			boolean existsAsIsolatedCategpry = isolatedCategory != null;

			if (existsAsIsolatedCategpry) {
				return isolatedCategory;

			}
			baseCategory.setName(StringHelper.toUpperCamelCase(baseCategoryName));
			return vendibleCategoryRepository.save(baseCategory);
		}

		VendibleCategory persistedBaseCategory = vendibleCategoryRepository.findByHierarchy(baseCategoryName,
				firstParentName, secondParentName);

		boolean hierachyExists = persistedBaseCategory != null;
		categoryParentAux = null;

		if (!hierachyExists) {
			hierachy.forEach(category -> {
				boolean categoryHasParent = category.getParent() != null;
				Optional<VendibleCategory> grandParentOpt = Optional.ofNullable(category.getParent())
						.map(VendibleCategory::getParent);
				Optional<VendibleCategory> categoryOpt = categoryHasParent
						? Optional.ofNullable(vendibleCategoryRepository.findByHierarchy(category.getName(),
								category.getParent().getName(),
								grandParentOpt.isPresent() ? grandParentOpt.get().getName() : null))
						: vendibleCategoryRepository.findByNameIgnoreCase(category.getName());
				boolean isCategoryPersisted = categoryOpt.isPresent();
				category.setName(StringHelper.toUpperCamelCase(category.getName()));
				category.setParent(categoryParentAux);

				if (!isCategoryPersisted) {
					categoryParentAux = vendibleCategoryRepository.save(category);
				} else {
					boolean newCategoryHasDifferentParent = category.getParent() != null
							&& !categoryOpt.get().getParent().equals(category.getParent());

					if (newCategoryHasDifferentParent) {
						categoryParentAux = vendibleCategoryRepository.save(category);
					} else {
						categoryParentAux = categoryOpt.get();
					}
				}
			});

			return categoryParentAux;

		}

		if (firstParentName != null && secondParentName != null) {
			return persistedBaseCategory;
		}

		baseCategory.setParent(persistedBaseCategory.getParent());
		return vendibleCategoryRepository.save(baseCategory);

	}

	private Point getUserLocation(Long userId) {

		String getUserFieldUrl = microServicioUsuarioUrl + (UsersControllerUrls.GET_USUARIO_FIELD
				.replace("{userId}", userId.toString()).replace("{fieldName}", "location"));

		Map<String, Object> responseMap = restTemplate.getForObject(getUserFieldUrl, Map.class);

		ArrayList coordsArray = (ArrayList) responseMap.get("coordinates");

		double x = (double) coordsArray.get(0);
		double y = (double) coordsArray.get(1);

		GeometryFactory geometryFactory = new GeometryFactory();
		return geometryFactory.createPoint(new Coordinate(x, y));

	}

	@Transactional
	private Vendible persistVendible(String vendibleType, Vendible vendible) {
		return vendibleType.equals(VendibleType.SERVICIO.name()) ? this.servicioRepository.save(vendible)
				: productoRepository.save(vendible);
	}

	public void requestPostStateChange(Long proveedorId, Long vendibleId, PostState state, HttpServletRequest request) {
		String url = microServicioUsuarioUrl + AdminControllerUrls.ADMIN_POST_BY_ID
				.replace("{id}", proveedorId.toString()).replace("{vendibleId}", vendibleId.toString());

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		ProveedorVendibleAdminDTO body = new ProveedorVendibleAdminDTO();
		body.setState(state);

		HttpEntity entity = new HttpEntity(body, headers);

		restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
	}

	public Vendible save(Vendible vendible, String vendibleType, Long proveedorId, HttpServletRequest request)
			throws UserNotFoundException, CantCreateException {

		boolean hasVendibleToLink = !vendible.getProveedoresVendibles().isEmpty();

		if (proveedorId == null || !hasVendibleToLink) {
			throw new CantCreateException();
		}

		ProveedorVendibleAccesor firstPv = vendible.getProveedoresVendibles()
				.toArray(new ProveedorVendibleAccesor[0])[0];

		VendibleCategory vendibleCategory = (VendibleCategory) firstPv.getCategory();

		if (Optional.ofNullable(vendibleCategory).isEmpty()
				|| Optional.ofNullable(vendibleCategory.getName()).isEmpty()) {
			throw new CantCreateException();
		}

		VendibleCategoryAccesor addedCategory = this.persistCategoryHierachy(vendibleCategory);
		firstPv.setCategory(addedCategory);

		Vendible addedVendible;

		try {
			addedVendible = this.findVendibleEntityByNombre(vendible.getNombre());
		} catch (VendibleNotFoundException e) {
			addedVendible = this.persistVendible(vendibleType, vendible);
		}

		if (!securityHelper.isResponseContentTypeValid(firstPv.getImagenUrl(), "image")) {
			throw new CantCreateException();
		}
		ProveedorType proveedorType = vendibleType.equals(VendibleType.SERVICIO.toString()) ? ProveedorType.SERVICIOS
				: ProveedorType.PRODUCTOS;

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microServicioUsuarioUrl)
				.path(UsersControllerUrls.GET_PROVEEDOR).queryParam("id", proveedorId)
				.queryParam("proveedorType", proveedorType);

		String usuarioExistsUrl = builder.toUriString();

		ResponseEntity<Void> getUsuarioResponse = restTemplate.getForEntity(usuarioExistsUrl, Void.class);

		if (getUsuarioResponse.getStatusCode().is2xxSuccessful()) {

			String addVendibleUrl = microServicioUsuarioUrl
					+ UsersControllerUrls.PROVEEDOR_VENDIBLE.replace("{proveedorId}", proveedorId.toString())
							.replace("{vendibleId}", addedVendible.getId().toString());

			ProveedorVendibleAccesor pv = (ProveedorVendibleAccesor) vendible.getProveedoresVendibles().toArray()[0];
			pv.setState(PostState.IN_REVIEW);

			if (!pv.getOffersInCustomAddress()) {
				Point proveedorLocation = this.getUserLocation(proveedorId);
				pv.setLocation(proveedorLocation);
			}

			ResponseEntity<Void> addVendibleResponse = restTemplate.postForEntity(addVendibleUrl, pv, Void.class);

			requestPostStateChange(proveedorId, addedVendible.getId(), PostState.ACTIVE, request);

			return addVendibleResponse.getStatusCodeValue() == 200 ? addedVendible : null;

		} else {
			throw new UserNotFoundException();
		}
	}

	@Transactional
	public Vendible update(String nombre, Long vendibleId, String vendibleType) throws VendibleNotFoundException {
		Optional<Vendible> toUpdateVendibleOpt = vendibleRepository.findById(vendibleId);
		if (toUpdateVendibleOpt.isPresent()) {
			String vendibleRealType = this.getVendibleTypeById(vendibleId);

			if (!vendibleRealType.equalsIgnoreCase(vendibleType)) {
				throw new VendibleNotFoundException();
			}

			Vendible toUpdateVendible = toUpdateVendibleOpt.get();

			toUpdateVendible.setNombre(nombre);

			Vendible updatedVendible = vendibleType.equals(VendibleType.SERVICIO.toString())
					? servicioRepository.save(toUpdateVendible)
					: productoRepository.save(toUpdateVendible);

			return updatedVendible;
		}

		throw new VendibleNotFoundException();
	}

	public void deleteById(Long id) throws VendibleNotFoundException {
		try {
			vendibleRepository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new VendibleNotFoundException();
		}
	}

	public Vendible findVendibleEntityById(Long vendibleId) throws VendibleNotFoundException {
		Optional<Vendible> vendibleOpt = vendibleRepository.findById(vendibleId);
		return vendibleOpt.map(vendible -> vendible).orElseThrow(() -> new VendibleNotFoundException());
	}

	public Vendible findVendibleEntityByNombre(String nombre) throws VendibleNotFoundException {
		Optional<Vendible> vendibleOpt = vendibleRepository.findByNombre(nombre);
		return vendibleOpt.map(vendible -> vendible).orElseThrow(() -> new VendibleNotFoundException());
	}

	public VendibleDTO findById(Long vendibleId) throws VendibleNotFoundException {
		Optional<Vendible> vendibleOpt = vendibleRepository.findById(vendibleId);
		if (vendibleOpt.isPresent()) {
			Vendible vendible = vendibleOpt.get();
			VendibleDTO vendibleDTO = new VendibleDTO(vendible.getId(), vendible.getNombre());

			Set<ProveedorVendibleDTO> proveedoresVendibles = vendible.getProveedoresVendibles().stream()
					.map(proveedorVendible -> {
						ProveedorAccessor proveedor = proveedorVendible.getProveedor();
						ProveedorVendibleDTO proveedorVendibleDTO = new ProveedorVendibleDTO(vendible.getNombre(),
								proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
								proveedorVendible.getImagenUrl(), proveedorVendible.getStock(),
								new ProveedorDTO(proveedor));
						return proveedorVendibleDTO;
					}).collect(Collectors.toSet());

			vendibleDTO.setProveedores(proveedoresVendibles);
			return vendibleDTO;
		}
		throw new VendibleNotFoundException();
	}

	public String getVendibleTypeById(Long vendibleId) {
		try {
			findById(vendibleId);
			return vendibleRepository.getVendibleTypeById(vendibleId);
		} catch (VendibleNotFoundException e) {
			return "";
		}

	}

	public VendibleCategory findCategoryById(Long categoryId) {
		Optional<VendibleCategory> valueOpt = vendibleCategoryRepository.findById(categoryId);
		return valueOpt.isPresent() ? valueOpt.get() : null;
	}

	public List<String> getCategoryHierachy(VendibleCategory baseCategory) {
		VendibleCategory category = vendibleCategoryRepository.findById(baseCategory.getId()).get();

		return VendibleHelper.fetchHierachyForCategory(category).stream().map(cat -> cat.getName())
				.collect(Collectors.toList());

	}

	public VendiblesResponseDTO findByNombreAsc(String nombre, Long categoryId,
			VendibleFetchingMethodResolver repositoryMethodResolver, HttpServletRequest request) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();

		String userRole = (String) ((Map<String, Object>) this.readJwtPayload(request)).get("role");

		repositoryMethodResolver.getFindByNombreRepositoryMethod(nombre, categoryId, userRole).get().stream()
				.forEach(vendible -> {
					Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper.getProveedoresVendibles(
							response, vendible, !userRole.equals(RolesNames.RolesValues.ADMIN.toString()));
					if (!proveedoresVendibles.isEmpty()) {
						response.getVendibles().put(vendible.getNombre(), proveedoresVendibles);
						vendible.getProveedoresVendibles().forEach(proveedorVendible -> {
							CategorizableObject categorizableProveedorVendible = (CategorizableObject) proveedorVendible;
							VendibleHelper.addCategoriasToResponse(categorizableProveedorVendible, response);
						});
					}
				});

		return response;
	}
}