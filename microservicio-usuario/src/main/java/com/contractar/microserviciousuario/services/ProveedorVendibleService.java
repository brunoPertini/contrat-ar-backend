package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.contractar.microservicioadapter.entities.VendibleAccesor;
import com.contractar.microservicioadapter.enums.PlanType;
import com.contractar.microservicioadapter.enums.PostState;
import com.contractar.microserviciocommons.constants.controllers.SecurityControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleFilter;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleUpdateDTO;
import com.contractar.microserviciocommons.dto.usuario.ProveedorDTO;
import com.contractar.microserviciocommons.dto.vendibles.ProveedorVendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.SimplifiedVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleProveedoresDTO;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyBindedException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleUpdateRuntimeException;
import com.contractar.microserviciocommons.helpers.DistanceCalculator;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.reflection.ReflectionHelper;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciousuario.admin.dtos.PostsResponseDTO;
import com.contractar.microserviciousuario.admin.dtos.ProveedorVendibleAdminDTO;
import com.contractar.microserviciousuario.dtos.DistanceProveedorDTO;
import com.contractar.microserviciousuario.filters.FilterChainCreator;
import com.contractar.microserviciousuario.models.Plan;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciousuario.models.ProveedorVendibleId;
import com.contractar.microserviciousuario.repository.ProveedorVendibleCustomRepository;
import com.contractar.microserviciousuario.repository.ProveedorVendibleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ProveedorVendibleService {
	@Autowired
	private ProveedorVendibleRepository repository;

	@Autowired
	private ProveedorVendibleCustomRepository customRepository;

	@Autowired
	private RestTemplate httpClient;

	@Autowired
	private SecurityHelper securityHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${microservicio-vendible.url}")
	private String SERVICIO_VENDIBLE_URL;

	@Value("${microservicio-usuario.url}")
	private String SERVICIO_USUARIO_URL;

	@Value("${microservicio-security.url}")
	private String SERVICIO_SECURITY_URL;

	private static int SLIDER_MIN_PRICE;
	private static int SLIDER_MAX_PRICE;

	private List<Double> distancesForSlider;

	private List<Integer> pricesForSlider;

	public List<Integer> getPricesForSlider() {
		return pricesForSlider;
	}

	public void setPricesForSlider(List<Integer> pricesForSlider) {
		this.pricesForSlider = pricesForSlider;
	}

	public List<Double> getDistancesForSlider() {
		return distancesForSlider;
	}

	public void setDistancesForSlider(List<Double> distancesForSlider) {
		this.distancesForSlider = distancesForSlider;
	}

	public ProveedorVendible save(ProveedorVendible post) {
		return repository.save(post);
	}

	public ProveedorVendible findById(ProveedorVendibleId id) throws VendibleNotFoundException {
		return this.repository.findById(id).orElseThrow(VendibleNotFoundException::new);
	}

	public ProveedorVendible bindVendibleToProveedor(VendibleAccesor vendible, Proveedor proveedor,
			ProveedorVendible proveedorVendible) throws VendibleAlreadyBindedException {
		ProveedorVendibleId id = new ProveedorVendibleId(proveedor.getId(), vendible.getId());
		if (repository.findById(id).isPresent()) {
			throw new VendibleAlreadyBindedException();
		}
		proveedorVendible.setProveedor(proveedor);
		proveedorVendible.setVendible(vendible);
		proveedorVendible.setId(id);
		return repository.save(proveedorVendible);
	}

	public void unBindVendible(Long vendibleId, Long proveedorId) throws VendibleNotFoundException {
		try {
			ProveedorVendibleId id = new ProveedorVendibleId(proveedorId, vendibleId);
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException ex) {
			throw new VendibleNotFoundException();
		}
	}

	/**
	 * Checks the only state change flow a proveedor can make without explicit admin
	 * approval.
	 * 
	 * @param vendible
	 * @param newData
	 * @throws VendibleUpdateRuntimeException
	 */
	private void handlePostStateChange(ProveedorVendible vendible, ProveedorVendibleUpdateDTO newData)
			throws VendibleUpdateRuntimeException {
		boolean isChangingToPaused = vendible.getState().equals(PostState.ACTIVE)
				&& newData.getState().equals(PostState.PAUSED);

		boolean isChangingToActive = vendible.getState().equals(PostState.PAUSED)
				&& newData.getState().equals(PostState.ACTIVE);

		if (!isChangingToPaused && !isChangingToActive) {
			throw new VendibleUpdateRuntimeException();
		}
	}

	private void performPostUpdate(ProveedorVendible vendible, ProveedorVendibleUpdateDTO newData)
			throws VendibleUpdateException {
		try {
			ReflectionHelper.applySetterFromExistingFields(newData, vendible,
					ReflectionHelper.getObjectClassFullName(newData),
					ReflectionHelper.getObjectClassFullName(vendible));
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			throw new VendibleUpdateException();
		}
		repository.save(vendible);
	}

	public void updateVendible(Long vendibleId, Long proveedorId, ProveedorVendibleUpdateDTO newData,
			HttpServletRequest request) throws VendibleNotFoundException, VendibleUpdateException,
			InvocationTargetException, IllegalAccessException, ClassNotFoundException {
		if (newData.getImagenUrl() != null
				&& !securityHelper.isResponseContentTypeValid(newData.getImagenUrl(), "image")) {
			throw new VendibleUpdateException();
		}

		Map<String, Object> dtoRawFields = ReflectionHelper.getObjectFields(newData);
		
		boolean isChangingState = Optional.ofNullable(newData.getState()).isPresent();

		boolean changesNeedApproval = !isChangingState && dtoRawFields.keySet().stream()
				.anyMatch(objectField -> ProveedorVendibleUpdateDTO.proveedorVendibleUpdateStrategy().get(objectField));

		

		ProveedorVendibleId id = new ProveedorVendibleId(proveedorId, vendibleId);
		ProveedorVendible vendible = this.findById(id);

		if (!isChangingState && !changesNeedApproval) {
			performPostUpdate(vendible, newData);
		} else {
			if (isChangingState) {
				handlePostStateChange(vendible, newData);
				performPostUpdate(vendible, newData);
			} else {
				String url = SERVICIO_VENDIBLE_URL + VendiblesControllersUrls.INTERNAL_POST_BY_ID
						.replace("{vendibleId}", vendibleId.toString()).replace("{proveedorId}", proveedorId.toString());

				HttpHeaders headers = new HttpHeaders();
				headers.set("Authorization", request.getHeader("Authorization"));

				HttpEntity<ProveedorVendibleUpdateDTO> entity = new HttpEntity<>(
						new ProveedorVendibleUpdateDTO(PostState.ACTIVE), headers);

				httpClient.exchange(url, HttpMethod.PUT, entity, Void.class);

				newData.setState(PostState.IN_REVIEW);
				performPostUpdate(vendible, newData);
			}

		}
	}

	@SuppressWarnings("unchecked")
	public ProveedorVendiblesResponseDTO getProveedorVendiblesInfo(Long proveedorId) {
		List<ProveedorVendible> results = repository.getProveedorVendibleInfo(proveedorId);

		ProveedorVendiblesResponseDTO response = new ProveedorVendiblesResponseDTO();

		for (ProveedorVendible pv : results) {
			SimplifiedVendibleDTO simplifiedVendibleDTO = new SimplifiedVendibleDTO();

			String getVendibleHierachyStringUrl = (SERVICIO_VENDIBLE_URL
					+ VendiblesControllersUrls.GET_CATEGORY_HIERACHY);

			List<String> categoryNames = pv.getCategory() != null
					? httpClient.postForObject(getVendibleHierachyStringUrl, pv.getCategory(), List.class)
					: List.of();

			simplifiedVendibleDTO.setVendibleId(pv.getVendible().getId());
			simplifiedVendibleDTO.setVendibleNombre(pv.getVendible().getNombre());
			simplifiedVendibleDTO.setDescripcion(pv.getDescripcion());
			simplifiedVendibleDTO.setImagenUrl(StringUtils.isEmpty(pv.getImagenUrl()) ? null : pv.getImagenUrl());
			simplifiedVendibleDTO.setCategoryNames(categoryNames);
			simplifiedVendibleDTO.setPrecio(pv.getPrecio());
			simplifiedVendibleDTO.setStock(pv.getStock());
			simplifiedVendibleDTO.setTipoPrecio(pv.getTipoPrecio());
			simplifiedVendibleDTO.setOffersDelivery(pv.getOffersDelivery());
			simplifiedVendibleDTO.setOffersInCustomAddress(pv.getOffersInCustomAddress());
			simplifiedVendibleDTO.setLocation(pv.getLocation());
			simplifiedVendibleDTO.setState(pv.getState());

			VendibleHelper.addCategoriasToResponse(pv, response);

			response.getVendibles().add(simplifiedVendibleDTO);
		}

		return response;

	}

	private void setMinAndMaxForSlider(VendibleProveedoresDTO response) {
		if (!pricesForSlider.isEmpty() && !distancesForSlider.isEmpty()) {
			Collections.sort(pricesForSlider);
			Collections.sort(distancesForSlider);

			Double firstDistance = distancesForSlider.get(0);
			Double lastDistance = distancesForSlider.get(distancesForSlider.size() - 1);
			boolean shouldNotSetDistanceSlider = firstDistance.equals(lastDistance);

			if (shouldNotSetDistanceSlider) {
				response.setMinDistance(null);
				response.setMaxDistance(null);
			} else {
				response.setMinDistance(firstDistance);
				response.setMaxDistance(lastDistance);
			}

			Integer firstPrice = pricesForSlider.get(0);
			Integer lastPrice = pricesForSlider.get(pricesForSlider.size() - 1);
			boolean shouldNotSetPriceSlider = firstPrice.equals(lastPrice);

			if (shouldNotSetPriceSlider) {
				response.setMinPrice(null);
				response.setMaxPrice(null);
			} else {
				response.setMinPrice(pricesForSlider.get(0));
				response.setMaxPrice(pricesForSlider.get(pricesForSlider.size() - 1));
			}
		}
	}

	private <T> List<T> getSublistForPagination(Pageable pageRequest, List<T> sourceList) {
		if (pageRequest.getOffset() >= sourceList.size()) {
			return sourceList;
		}

		int start = (int) pageRequest.getOffset();

		int end = Math.min((start + pageRequest.getPageSize()), sourceList.size());

		return sourceList.subList(start, end);
	}

	public Object getUserPayloadFromToken(HttpServletRequest request) {
		String getPayloadUrl = SERVICIO_SECURITY_URL + SecurityControllerUrls.GET_USER_PAYLOAD_FROM_TOKEN;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Object> getPayloadResponse = httpClient.exchange(getPayloadUrl, HttpMethod.GET, entity,
				Object.class);

		return getPayloadResponse.getBody();
	}

	private Point getUserLocationFromHeaders(HttpServletRequest request) {
		Map<String, Object> headersPayload = ((Map<String, Object>) getUserPayloadFromToken(request));

		Long userId = Long.parseLong((String) headersPayload.get("id"));

		String getUserFieldUrl = SERVICIO_USUARIO_URL + (UsersControllerUrls.GET_USUARIO_FIELD
				.replace("{userId}", userId.toString()).replace("{fieldName}", "location"));

		Object userLocationObj = httpClient.getForObject(getUserFieldUrl, Object.class);

		return objectMapper.convertValue(userLocationObj, Point.class);
	}

	private boolean proveedorMatchesPlanConstraint(ProveedorVendible post, Point userLocation) {
		if (post.getProveedor().getSuscripcion().getPlan().getType().equals(PlanType.PAID)) {
			return true;
		}

		boolean isProviderLocationOk = DistanceCalculator.isPointInsideRadius(post.getProveedor().getLocation(),
				Plan.FREE_PLAN_RADIUS, userLocation);

		boolean isPostLocationOk = DistanceCalculator.isPointInsideRadius(post.getLocation(), Plan.FREE_PLAN_RADIUS,
				userLocation);

		// If the plan limits some offering type, it has to be closed
		post.setOffersDelivery(isProviderLocationOk);
		post.setOffersInCustomAddress(isPostLocationOk);

		if (post.getOffersDelivery() && post.getOffersInCustomAddress()) {
			return isProviderLocationOk && isPostLocationOk;
		}

		if (post.getOffersDelivery()) {
			return isProviderLocationOk;
		}

		return isPostLocationOk;
	}

	private Page<ProveedorVendible> findAllByLocationAndPlanConstraints(Long vendibleId, Point userLocation,
			Pageable pageable) {

		List<ProveedorVendible> filteredPosts = repository.getPostsOfProveedoresWithValidSubscription(vendibleId)
				.stream().filter(post -> {
					return proveedorMatchesPlanConstraint(post, userLocation);
				}).collect(Collectors.toList());

		List<ProveedorVendible> pageContent = getSublistForPagination(pageable, filteredPosts);
		return new PageImpl<>(pageContent, pageable, filteredPosts.size());
	}

	/**
	 * Gets all the providers that offer the given vendible. They should comply with
	 * their subscription constraints, and, if they are present, with the distances
	 * and prices filters. As this is invoked by a client, only posts with ACTIVE
	 * state should be filtered
	 * 
	 * @param vendibleId  Product or service id
	 * @param minDistance Minimum distance (from client's location) a vendible is
	 *                    offered in
	 * @param maxDistance Maximum distance (from client's location) a vendible is
	 *                    offered in
	 * @param minPrice    Minimum price the vendible must have
	 * @param maxPrice    Maximum price the vendible must have
	 * @param request     Used to take client's user id and with it it's location
	 * @param pageable    pagination info
	 */
	public VendibleProveedoresDTO getProveedoreVendiblesInfoForVendible(Long vendibleId, Double minDistance,
			Double maxDistance, Integer minPrice, Integer maxPrice, HttpServletRequest request, Pageable pageable) {

		VendibleProveedoresDTO response = new VendibleProveedoresDTO();

		Point userLocation = getUserLocationFromHeaders(request);

		Page<ProveedorVendible> results = findAllByLocationAndPlanConstraints(vendibleId, userLocation, pageable);

		FilterChainCreator chainCreator = new FilterChainCreator(minDistance, maxDistance, null, minPrice, maxPrice,
				null);

		boolean chainNotExists = chainCreator.getFilterChain() == null;

		Set<ProveedorDTO> proveedores = new LinkedHashSet<>();

		this.pricesForSlider = new ArrayList<>();
		this.distancesForSlider = new ArrayList<>();
		Map<Long, Double> distances = new HashMap<>();

		ArrayList<DistanceProveedorDTO> posts = (ArrayList<DistanceProveedorDTO>) results.filter(proveedorVendible -> {
			double distanceNotRounded = DistanceCalculator.resolveDistanceFromClient(userLocation, proveedorVendible);

			BigDecimal distanceBd = BigDecimal.valueOf(distanceNotRounded).setScale(2, RoundingMode.HALF_UP);

			double distance = distanceBd.doubleValue();

			pricesForSlider.add(proveedorVendible.getPrecio());
			distancesForSlider.add(distance);

			if (!chainNotExists) {
				chainCreator.setToCompareDistance(distance);
				chainCreator.setToComparePrice(proveedorVendible.getPrecio());
			}

			boolean filterResult = chainNotExists || chainCreator.runChain();

			if (filterResult) {
				distances.put(proveedorVendible.getId().getProveedorId(), distance);
			}

			return filterResult;

		}).map(proveedorVendible -> {
			DistanceProveedorDTO distanceDTO = new DistanceProveedorDTO(proveedorVendible.getId().getVendibleId(),
					proveedorVendible.getId().getProveedorId(), proveedorVendible.getVendible().getNombre(),
					proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
					proveedorVendible.getTipoPrecio(), proveedorVendible.getOffersDelivery(),
					proveedorVendible.getOffersInCustomAddress(), proveedorVendible.getImagenUrl(),
					proveedorVendible.getStock(), proveedorVendible.getCategory().getId(),
					distances.get(proveedorVendible.getId().getProveedorId()));

			distanceDTO.setPlanId(proveedorVendible.getProveedor().getSuscripcion().getPlan().getId());

			ProveedorDTO toAddProveedor = new ProveedorDTO(proveedorVendible.getProveedor());
			toAddProveedor.setLocation(proveedorVendible.getLocation());
			proveedores.add(toAddProveedor);

			return distanceDTO;
		}).stream().collect(Collectors.toList());

		setMinAndMaxForSlider(response);

		boolean shouldSortByPrice = minPrice != null || maxPrice != null;
		boolean shouldSortByDistance = minDistance != null || maxDistance != null;

		if (shouldSortByPrice || shouldSortByDistance) {
			ProveedorVendibleComparator comparator = new ProveedorVendibleComparator(shouldSortByPrice,
					shouldSortByDistance);
			posts.sort(comparator);
		}

		response.setVendibles(new PageImpl(posts, pageable, results.getTotalElements()));
		response.setProveedores(getSublistForPagination(pageable, new ArrayList<>(proveedores)));

		return response;

	}

	public PostsResponseDTO getPostsOfVendible(Long vendibleId, int page, int size,
			@Nullable ProveedorVendibleFilter filters) {

		// TODO: implementar caché acá
		List<ProveedorVendible> allResults = customRepository.get(vendibleId, filters);

		Pageable pageRequest = PageRequest.of(page, size);

		List<ProveedorVendible> subList = getSublistForPagination(pageRequest, allResults);

		if (!subList.isEmpty()) {
			SLIDER_MIN_PRICE = subList.get(0).getPrecio();
			SLIDER_MAX_PRICE = subList.get(subList.size() - 1).getPrecio();
		} else {
			SLIDER_MIN_PRICE = 0;
			SLIDER_MAX_PRICE = 0;
		}

		List<ProveedorVendibleAdminDTO> pageContent = subList.stream().map(ProveedorVendibleAdminDTO::new)
				.collect(Collectors.toList());

		PageImpl pageImpl = new PageImpl<>(pageContent, pageRequest, allResults.size());

		PostsResponseDTO response = new PostsResponseDTO(pageImpl);

		response.setMinPrice(SLIDER_MIN_PRICE);
		response.setMaxPrice(SLIDER_MAX_PRICE);

		return response;

	}
}
