package com.contractar.microserviciovendible.filters;

import java.util.Map;

public class DistanceFilterHandler extends FilterHandlerBaseClass{

	@Override
	boolean handleFilter(Map<String, Object> args) {
		Map<String, Double> distanceArgs = (Map<String, Double>) args.get(FilterChainCreator.FILTER_NAME_DISTANCE);
		double firstDistance = (double) distanceArgs.get(FilterChainCreator.FILTER_NAME_FIRST_DISTANCE);
		double secondDistance = (double) distanceArgs.get(FilterChainCreator.FILTER_NAME_SECOND_DISTANCE);
		double toCompareDistance = (double) distanceArgs.get(FilterChainCreator.FILTER_NAME_TO_COMPARE_DISTANCE);
		
		return toCompareDistance >= firstDistance && toCompareDistance <= secondDistance;
	}

}
