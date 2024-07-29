package com.contractar.microserviciocommons.dto.proveedorvendible;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.contractar.microservicioadapter.enums.PriceTypeInterface;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProveedorVendibleFilteringStrategy {
	private Map<String, Predicate> fieldsStrategies = new HashMap<>();

	public static final String FIELD_PROVEEDOR_NAME = "proveedorName";

	public static final String FIELD_PROVEEDOR_SURNAME = "proveedorSurname";

	public static final String FIELD_CATEGORY_NAME = "categoryName";

	public static final String FIELD_MIN_PRICE = "minPrice";

	public static final String FIELD_MAX_PRICE = "maxPrice";

	public static final String FIELD_MIN_STOCK = "minStock";

	public static final String FIELD_MAX_STOCK = "maxStock";

	public static final String FIELD_OFFERS_DELIVERY = "offersDelivery";

	public static final String FIELD_OFFERS_CUSTOM_ADDRESS = "offersInCustomAddress";

	public static final String FIELD_PRICE_TYPE = "priceType";

	private Path ddbbSourceObject;

	public ProveedorVendibleFilteringStrategy() {
	}

	private Function<Object, Boolean> isValueNull = (value) -> Optional.ofNullable(value).isEmpty();

	public ProveedorVendibleFilteringStrategy(CriteriaBuilder cb, Root<?> root, ProveedorVendibleFilter postsFilters) {

		Optional.ofNullable(postsFilters).ifPresent(filters -> {
			processStringLikeNotExactCondition(FIELD_PROVEEDOR_NAME, root, cb, List.of("proveedor", "name"),
					filters.getProveedorName());
			processStringLikeNotExactCondition(FIELD_PROVEEDOR_SURNAME, root, cb, List.of("proveedor", "surname"),
					filters.getProveedorSurname());
			processStringLikeNotExactCondition(FIELD_CATEGORY_NAME, root, cb, List.of("category", "name"),
					filters.getCategoryName());

			processIntegerGreaterOrEqualCondition(FIELD_MIN_PRICE, root, cb, List.of("precio"), filters.getMinPrice());
			processIntegerLowerOrEqualCondition(FIELD_MAX_PRICE, root, cb, List.of("precio"), filters.getMaxPrice());

			processIntegerGreaterOrEqualCondition(FIELD_MIN_STOCK, root, cb, List.of("stock"), filters.getMinStock());
			processIntegerLowerOrEqualCondition(FIELD_MAX_STOCK, root, cb, List.of("stock"), filters.getMaxStock());

			processBooleanCondition(FIELD_OFFERS_DELIVERY, root, cb, List.of(FIELD_OFFERS_DELIVERY),
					filters.isOffersDelivery());
			processBooleanCondition(FIELD_OFFERS_CUSTOM_ADDRESS, root, cb, List.of(FIELD_OFFERS_CUSTOM_ADDRESS),
					filters.isOffersInCustomAddress());

			processPriceTypeEqualsCondition(FIELD_PRICE_TYPE, root, cb, List.of("tipoPrecio"), filters.getPriceType());
		});
	}

	private void processStringLikeNotExactCondition(String mapKey, Root<?> root, CriteriaBuilder cb,
			List<String> rootAttributes, String fieldValue) {
		if (!isValueNull.apply(fieldValue)) {
			ddbbSourceObject = root;

			for (String attribute : rootAttributes) {
				ddbbSourceObject = ddbbSourceObject.get(attribute);
			}

			this.fieldsStrategies.put(mapKey, cb.like(ddbbSourceObject, "%" + fieldValue + "%"));

		}
	}

	private void processIntegerGreaterOrEqualCondition(String mapKey, Root<?> root, CriteriaBuilder cb,
			List<String> rootAttributes, Integer value) {
		if (!isValueNull.apply(value)) {
			ddbbSourceObject = root;

			for (String attribute : rootAttributes) {
				ddbbSourceObject = ddbbSourceObject.get(attribute);
			}

			this.fieldsStrategies.put(mapKey, cb.greaterThanOrEqualTo(ddbbSourceObject, value));
		}
	}

	private void processIntegerLowerOrEqualCondition(String mapKey, Root<?> root, CriteriaBuilder cb,
			List<String> rootAttributes, Integer value) {
		if (!isValueNull.apply(value)) {
			ddbbSourceObject = root;

			for (String attribute : rootAttributes) {
				ddbbSourceObject = ddbbSourceObject.get(attribute);
			}

			this.fieldsStrategies.put(mapKey, cb.lessThanOrEqualTo(ddbbSourceObject, value));
		}
	}

	private void processBooleanCondition(String mapKey, Root<?> root, CriteriaBuilder cb, List<String> rootAttributes,
			Boolean value) {
		if (!isValueNull.apply(value)) {
			ddbbSourceObject = root;

			for (String attribute : rootAttributes) {
				ddbbSourceObject = ddbbSourceObject.get(attribute);
			}

			this.fieldsStrategies.put(mapKey, cb.equal(ddbbSourceObject, value));
		}
	}

	private void processPriceTypeEqualsCondition(String mapKey, Root<?> root, CriteriaBuilder cb,
			List<String> rootAttributes, PriceTypeInterface value) {
		if (!isValueNull.apply(value)) {
			ddbbSourceObject = root;

			for (String attribute : rootAttributes) {
				ddbbSourceObject = ddbbSourceObject.get(attribute);
			}

			this.fieldsStrategies.put(mapKey, cb.equal(ddbbSourceObject, value));
		}
	}

	public Path<?> getDdbbSourceObject() {
		return ddbbSourceObject;
	}

	public void setDdbbSourceObject(Path<?> ddbbSourceObject) {
		this.ddbbSourceObject = ddbbSourceObject;
	}

	public List<Predicate> getAllStrategies() {
		return this.fieldsStrategies.values().stream().collect(Collectors.toList());

	}
}
