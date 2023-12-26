package com.contractar.microserviciovendible.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.dto.ProveedorDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.ProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.proveedorvendible.SimplifiedProveedorVendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendibleDTO;
import com.contractar.microserviciocommons.dto.vendibles.VendiblesResponseDTO;
import com.contractar.microserviciocommons.dto.vendibles.category.CategoryHierarchy;
import com.contractar.microserviciocommons.dto.vendibles.category.VendibleCategoryDTO;
import com.contractar.microserviciocommons.exceptions.UserNotFoundException;
import com.contractar.microserviciocommons.exceptions.vendibles.CantCreateException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleAlreadyExistsException;
import com.contractar.microserviciocommons.exceptions.vendibles.VendibleNotFoundException;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.proveedores.ProveedorType;
import com.contractar.microserviciocommons.vendibles.VendibleHelper;
import com.contractar.microserviciocommons.vendibles.VendibleType;
import com.contractar.microserviciousuario.models.Proveedor;
import com.contractar.microserviciousuario.models.ProveedorVendible;
import com.contractar.microserviciovendible.models.Vendible;
import com.contractar.microserviciovendible.models.VendibleCategory;
import com.contractar.microserviciovendible.repository.ProductoRepository;
import com.contractar.microserviciovendible.repository.ServicioRepository;
import com.contractar.microserviciovendible.repository.VendibleCategoryRepository;
import com.contractar.microserviciovendible.repository.VendibleRepository;
import com.contractar.microserviciovendible.services.resolvers.VendibleFetchingMethodResolver;

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

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SecurityHelper securityHelper;

	@Transactional
	private VendibleCategory persistCategoryHierachy(VendibleCategory baseCategory) {
		ArrayList<VendibleCategory> hierachy = new ArrayList<VendibleCategory>();
		hierachy.add(baseCategory);

		Optional<VendibleCategory> parentOpt = Optional.ofNullable(baseCategory.getParent());
		while (parentOpt.isPresent()) {
			VendibleCategory parent = parentOpt.get();
			hierachy.add(parent);
			parentOpt = Optional.ofNullable(parent.getParent());
		}

		Collections.reverse(hierachy);

		boolean hierachyExists = vendibleCategoryRepository.hierachyExists(baseCategory.getId(),
				hierachy.get(0).getId(), hierachy.get(1).getId());

		if (!hierachyExists) {
			hierachy.forEach(category -> {
				vendibleCategoryRepository.save(category);
			});
			
			return vendibleCategoryRepository.findByName(baseCategory.getName()).get();
		} else {
			return vendibleCategoryRepository.findByNameAndParent(baseCategory.getName(),
					vendibleCategoryRepository.findByName(baseCategory.getParent().getName()));
		}

	}

	@Transactional
	private Vendible persistVendible(String vendibleType, Vendible vendible) {
		return vendibleType.equals(VendibleType.SERVICIO.name()) ? this.servicioRepository.save(vendible)
				: productoRepository.save(vendible);
	}

	public Vendible save(Vendible vendible, String vendibleType, Long proveedorId)
			throws VendibleAlreadyExistsException, UserNotFoundException, CantCreateException {
		try {

			VendibleCategory addedCategory = this.persistCategoryHierachy(vendible.getCategory());
			vendible.setCategory(addedCategory);
			Vendible addedVendible = this.persistVendible(vendibleType, vendible);

			boolean hasVendibleToLink = vendible.getProveedoresVendibles().size() > 0;

			if (proveedorId != null && hasVendibleToLink) {
				ProveedorVendible firstPv = vendible.getProveedoresVendibles().toArray(new ProveedorVendible[0])[0];
				if (!securityHelper.isResponseContentTypeValid(firstPv.getImagenUrl(), "image")) {
					throw new CantCreateException();
				}
				ProveedorType proveedorType = vendibleType.equals(VendibleType.SERVICIO.toString())
						? ProveedorType.SERVICIOS
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

					ProveedorVendible pv = (ProveedorVendible) vendible.getProveedoresVendibles().toArray()[0];

					ResponseEntity<Void> addVendibleResponse = restTemplate.postForEntity(addVendibleUrl, pv,
							Void.class);

					return addVendibleResponse.getStatusCodeValue() == 200 ? addedVendible : null;

				} else {
					throw new UserNotFoundException();
				}
			}

			return addedVendible;

		} catch (DataIntegrityViolationException e) {
			throw new VendibleAlreadyExistsException();
		} catch (CantCreateException e) {
			throw e;
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

	public VendibleDTO findById(Long vendibleId) throws VendibleNotFoundException {
		Optional<Vendible> vendibleOpt = vendibleRepository.findById(vendibleId);
		if (vendibleOpt.isPresent()) {
			Vendible vendible = vendibleOpt.get();
			VendibleDTO vendibleDTO = new VendibleDTO(vendible.getId(), vendible.getNombre());

			Set<ProveedorVendibleDTO> proveedoresVendibles = vendible.getProveedoresVendibles().stream()
					.map(proveedorVendible -> {
						Proveedor proveedor = proveedorVendible.getProveedor();
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

	public VendibleCategory findCategoryByName(String name) {
		Optional<VendibleCategory> valueOpt = vendibleCategoryRepository.findByName(name);
		return valueOpt.isPresent() ? valueOpt.get() : null;
	}

	public List<String> getCategoryHierachy(String categoryName) {
		VendibleCategory category = vendibleCategoryRepository.findByName(categoryName).map(c -> c)
				.orElseGet(() -> null);
		return VendibleHelper.fetchHierachyForCategory(category).stream().map(cat -> cat.getName())
				.collect(Collectors.toList());
	}

	public VendiblesResponseDTO findByNombreAsc(String nombre, String categoryName,
			VendibleFetchingMethodResolver repositoryMethodResolver) {
		VendiblesResponseDTO response = new VendiblesResponseDTO();

		repositoryMethodResolver.getFindByNombreRepositoryMethod(nombre, categoryName).get().stream()
				.forEach(vendible -> {
					Set<SimplifiedProveedorVendibleDTO> proveedoresVendibles = VendibleHelper
							.getProveedoresVendibles(response, vendible);
					if (proveedoresVendibles.size() > 0) {
						response.getVendibles().put(vendible.getNombre(), proveedoresVendibles);
						VendibleHelper.addCategoriasToResponse(vendible, response);
					}
				});

		return response;
	}
}