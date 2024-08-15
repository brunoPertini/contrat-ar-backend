package com.contractar.microserviciousuario.services;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
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

import com.contractar.microservicioadapter.dtos.AbstractProveedorVendibleDTOAccesor;
import com.contractar.microservicioadapter.entities.VendibleAccesor;
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

	public void updateVendible(Long vendibleId, Long proveedorId, ProveedorVendibleUpdateDTO newData)
			throws VendibleNotFoundException, VendibleUpdateException {
		if (newData.getImagenUrl() != null
				&& !securityHelper.isResponseContentTypeValid(newData.getImagenUrl(), "image")) {
			throw new VendibleUpdateException();
		}

		ProveedorVendibleId id = new ProveedorVendibleId(proveedorId, vendibleId);
		ProveedorVendible vendible = this.repository.findById(id).orElseThrow(() -> new VendibleNotFoundException());

		String dtoFullClassName = ProveedorVendibleUpdateDTO.class.getPackage().getName()
				+ ".ProveedorVendibleUpdateDTO";
		String entityFullClassName = ProveedorVendible.class.getPackage().getName() + ".ProveedorVendible";

		try {
			ReflectionHelper.applySetterFromExistingFields(newData, vendible, dtoFullClassName, entityFullClassName);
			repository.save(vendible);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException e) {
			throw new VendibleUpdateException();
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

			VendibleHelper.addCategoriasToResponse(pv, response);

			response.getVendibles().add(simplifiedVendibleDTO);
		}

		return response;

	}

	private void setMinAndMaxForSlider(VendibleProveedoresDTO response, List<Double> distances, List<Integer> prices) {
		Collections.sort(distances);
		Collections.sort(prices);
		response.setMinDistance(distances.get(0));
		response.setMaxDistance(distances.get(distances.size() - 1));
		response.setMinPrice(prices.get(0));
		response.setMaxPrice(prices.get(distances.size() - 1));
	}

	private <T> List<T> getSublistForPagination(Pageable pageRequest, List<T> sourceList) {
		int start = (int) pageRequest.getOffset();

		int end = Math.min((start + pageRequest.getPageSize()), sourceList.size());

		return sourceList.subList(start, end);
	}

	private Point getUserLocationFromHeaders(HttpServletRequest request) {
		String getClientIdUrl = SERVICIO_SECURITY_URL + SecurityControllerUrls.GET_USER_ID_FROM_TOKEN;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", request.getHeader("Authorization"));

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<Long> getClientIdResponse = httpClient.exchange(getClientIdUrl, HttpMethod.GET, entity,
				Long.class);

		Long clienteId = getClientIdResponse.getBody();

		String getUserFieldUrl = SERVICIO_USUARIO_URL + (UsersControllerUrls.GET_USUARIO_FIELD
				.replace("{userId}", clienteId.toString()).replace("{fieldName}", "location"));

		Object userLocationObj = httpClient.getForObject(getUserFieldUrl, Object.class);

		return objectMapper.convertValue(userLocationObj, Point.class);
	}

	private boolean proveedorMatchesPlanConstraint(ProveedorVendible post, Point userLocation) {
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

	/**
	 * Gets all the providers that offer the given vendible. They should comply with
	 * their subscription constraints, and, if they are present, with the distances
	 * and prices filters.
	 * 
	 * @param vendibleId  Product or service id
	 * @param minDistance Minimum distance (from client's location) a vendible is
	 *                    offered in
	 * @param maxDistance Maximum distance (from client's location) a vendible is
	 *                    offered in
	 * @param minPrice    Minimum price the vendible must have
	 * @param maxPrice    Maximum price the vendible must have
	 * @param request     Used to take client's user id and with it it's location
	 * @param pageable    pagination info TODO: REFACTOR
	 */
	public VendibleProveedoresDTO getProveedoreVendiblesInfoForVendible(Long vendibleId, Double minDistance,
			Double maxDistance, Integer minPrice, Integer maxPrice, HttpServletRequest request, Pageable pageable) {

		Point userLocation = getUserLocationFromHeaders(request);

		List<ProveedorVendible> postsWithPayedPlans = repository
				.getPostsOfProveedoresWithActiveAndPayedPlan(vendibleId, pageable).getContent();

		List<ProveedorVendible> postsWithFreePlans = repository
				.getPostsOfProveedoresWithActiveAndFreePlan(vendibleId, pageable).getContent()
				.stream()
				.filter(pv -> proveedorMatchesPlanConstraint(pv, userLocation)).collect(Collectors.toList());

		List<ProveedorVendible> results = new ArrayList<>();
		results.addAll(postsWithPayedPlans);
		results.addAll(postsWithFreePlans);

		FilterChainCreator chainCreator = new FilterChainCreator(minDistance, maxDistance, null, minPrice, maxPrice,
				null);

		boolean chainNotExists = chainCreator.getFilterChain() == null;

		ArrayList<AbstractProveedorVendibleDTOAccesor> posts = new ArrayList<>();
		Set<ProveedorDTO> proveedores = new LinkedHashSet<>();

		List<Double> toSortDistances = new ArrayList<>();

		List<Integer> toSortPrices = new ArrayList<>();

		results.forEach(proveedorVendible -> {
			double distance = DistanceCalculator.calculateDistance(userLocation.getX(),
					userLocation.getY(),
					proveedorVendible.getLocation().getX(),
					proveedorVendible.getLocation().getY());

			toSortDistances.add(distance);
			toSortPrices.add(proveedorVendible.getPrecio());

			if (!chainNotExists) {
				chainCreator.setToCompareDistance(distance);
				chainCreator.setToComparePrice(proveedorVendible.getPrecio());
			}

			boolean shouldAddInfo = chainNotExists || chainCreator.runChain();

			if (shouldAddInfo) {
				posts.add(new DistanceProveedorDTO(proveedorVendible.getVendible().getNombre(),
						proveedorVendible.getDescripcion(), proveedorVendible.getPrecio(),
						proveedorVendible.getImagenUrl(), proveedorVendible.getStock(),
						proveedorVendible.getProveedor().getId(), distance));

				ProveedorDTO toAddProveedor = new ProveedorDTO(proveedorVendible.getProveedor());
				toAddProveedor.setLocation(proveedorVendible.getLocation());

				proveedores.add(toAddProveedor);
			}
		});

		List<AbstractProveedorVendibleDTOAccesor> subList = getSublistForPagination(pageable, posts);

		boolean shouldSortByPrice = minPrice != null || maxPrice != null;
		boolean shouldSortByDistance = minDistance != null || maxDistance != null;

		if (shouldSortByPrice || shouldSortByDistance) {
			ProveedorVendibleComparator comparator = new ProveedorVendibleComparator(shouldSortByPrice,
					shouldSortByDistance);
			subList.sort(comparator);
		}

		VendibleProveedoresDTO response = new VendibleProveedoresDTO(new PageImpl<>(subList, pageable, posts.size()),
				proveedores);

		setMinAndMaxForSlider(response, toSortDistances, toSortPrices);

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
