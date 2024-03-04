package com.contractar.microserviciovendible.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class intends to be a Wrapper to isolate some specifics about FilterHandler instances. Some of its
 * tasks are:
 * 	1. Based on the parameters it receives in its constructor, creates the chain with the proper filters and order of execution.
 *  2. Manages the map of arguments, each one with a meaningful name, to be passed to the chain of responsibility.
 */
public class FilterChainCreator {

	public static final String FILTER_NAME_DISTANCE = "DISTANCE_FILTERS";

	public static final String FILTER_NAME_FIRST_DISTANCE = "DISTANCE_FILTERS_FIRST_DISTANCE";

	public static final String FILTER_NAME_SECOND_DISTANCE = "DISTANCE_FILTERS_SECOND_DISTANCE";

	public static final String FILTER_NAME_TO_COMPARE_DISTANCE = "FILTER_NAME_TO_COMPARE_DISTANCE";
	
	
	public static final String PRICE_FILTERS = "PRICE_FILTERS";

	public static final String PRICE_FILTERS_FIRST_PRICE = "PRICE_FILTERS_FIRST_PRICE";

	public static final String PRICE_FILTERS_SECOND_PRICE = "PRICE_FILTERS_SECOND_PRICE";

	public static final String FILTER_NAME_TO_COMPARE_PRICE = "FILTER_NAME_TO_COMPARE_PRICE";


	private FilterHandler filterChain;

	private Map<String, Object> chainArgs;
	
	private DistanceFilterHandler createFilterHandler(Double firstDistance, Double secondDistance, Double toCompareDistance) {
		boolean shouldCreateDistanceChain = firstDistance != null || secondDistance != null;

		if (shouldCreateDistanceChain) {
			DistanceFilterHandler handler = new DistanceFilterHandler();
			
			if (chainArgs == null) {
				chainArgs = new HashMap<>();
			}

			chainArgs.put(FILTER_NAME_DISTANCE, new HashMap<String, Double>());

			HashMap<String, Double> distanceArgs = (HashMap<String, Double>) chainArgs.get(FILTER_NAME_DISTANCE);

			distanceArgs.put(FILTER_NAME_FIRST_DISTANCE, firstDistance);

			distanceArgs.put(FILTER_NAME_SECOND_DISTANCE, secondDistance);

			distanceArgs.put(FILTER_NAME_TO_COMPARE_DISTANCE, toCompareDistance);
			
			return handler;
		}
		
		return null;
	}
	
	public FilterChainCreator() {
	}

	public FilterChainCreator(Double firstDistance, Double secondDistance, Double toCompareDistance) {
		this.filterChain = createFilterHandler(firstDistance,secondDistance,toCompareDistance);
	}
	
	public FilterChainCreator(Double firstDistance, Double secondDistance, Double toCompareDistance, Integer firstPrice,
			Integer secondPrice, Integer toComparePrice) {
		boolean shouldCreatePriceChain = firstPrice != null && secondPrice != null;
		
		if (shouldCreatePriceChain) {
			this.filterChain = new PriceFilterHandler();
			chainArgs = new HashMap<>();
			chainArgs.put(PRICE_FILTERS, new HashMap<String, Integer>());
			
			
			HashMap<String, Integer> priceArgs = (HashMap<String, Integer>) chainArgs.get(PRICE_FILTERS);
			
			priceArgs.put(PRICE_FILTERS_FIRST_PRICE, firstPrice);
			
			priceArgs.put(PRICE_FILTERS_SECOND_PRICE, secondPrice);
			
			priceArgs.put(FILTER_NAME_TO_COMPARE_PRICE, toComparePrice);
			
			this.filterChain.setNextHanlder(createFilterHandler(firstDistance, secondDistance, toCompareDistance));
		} else {
			this.filterChain = createFilterHandler(firstDistance, secondDistance, toCompareDistance);
		}
	}

	public Map<String, Object> getChainArgs() {
		return chainArgs;
	}

	public void setChainArgs(Map<String, Object> chainArgs) {
		this.chainArgs = chainArgs;
	}

	public FilterHandler getFilterChain() {
		return filterChain;
	}

	public void setFilterChain(FilterHandler filterChain) {
		this.filterChain = filterChain;
	}

	public void setToCompareDistance(double newValue) {
		Optional.ofNullable((HashMap<String, Double>) this.chainArgs.get(FILTER_NAME_DISTANCE)).ifPresent(distanceParams -> {
			distanceParams.put(FILTER_NAME_TO_COMPARE_DISTANCE, newValue);
		});
	}
	
	public void setToComparePrice(Integer newValue) {
		Optional.ofNullable((HashMap<String, Integer>) this.chainArgs.get(PRICE_FILTERS)).ifPresent(priceParams -> {
			priceParams.put(FILTER_NAME_TO_COMPARE_PRICE, newValue);
		});
	}
	
	public boolean runChain() {
		return filterChain.handleRequest(chainArgs);
	}
}
