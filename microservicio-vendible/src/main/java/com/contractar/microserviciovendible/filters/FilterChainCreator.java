package com.contractar.microserviciovendible.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FilterChainCreator {

	public static final String FILTER_NAME_DISTANCE = "DISTANCE_FILTERS";

	public static final String FILTER_NAME_FIRST_DISTANCE = "DISTANCE_FILTERS_FIRST_DISTANCE";

	public static final String FILTER_NAME_SECOND_DISTANCE = "DISTANCE_FILTERS_SECOND_DISTANCE";

	public static final String FILTER_NAME_TO_COMPARE_DISTANCE = "FILTER_NAME_TO_COMPARE_DISTANCE";

	private FilterHandler filterChain;

	private Map<String, Object> chainArgs;

	public FilterChainCreator() {
	}

	public FilterChainCreator(Double firstDistance, Double secondDistance, Double toCompareDistance) {
		boolean shouldCreateDistanceChain = firstDistance != null || secondDistance != null;

		if (shouldCreateDistanceChain) {
			this.filterChain = new DistanceFilterHandler();
			chainArgs = new HashMap<>();
			chainArgs.put(FILTER_NAME_DISTANCE, new HashMap<String, Double>());

			HashMap<String, Double> distanceArgs = (HashMap<String, Double>) chainArgs.get(FILTER_NAME_DISTANCE);

			distanceArgs.put(FILTER_NAME_FIRST_DISTANCE, firstDistance);

			distanceArgs.put(FILTER_NAME_SECOND_DISTANCE, secondDistance);

			distanceArgs.put(FILTER_NAME_TO_COMPARE_DISTANCE, toCompareDistance);
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
		((HashMap<String, Double>) this.chainArgs.get(FILTER_NAME_DISTANCE)).put(FILTER_NAME_TO_COMPARE_DISTANCE,
				newValue);
	}
	
	public boolean runChain() {
		return filterChain.handleRequest(chainArgs);
	}
}
