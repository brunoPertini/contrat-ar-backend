package com.contractar.microserviciovendible.filters;

import java.util.Map;

public class DistanceFilterHandler extends FilterHandlerBaseClass{

	@Override
	boolean handleFilter(Map<String, Object> args) {
		Map<String, Double> distanceArgs = (Map<String, Double>) args.get(FilterChainCreator.FILTER_NAME_DISTANCE);
		Double firstDistance = (Double) distanceArgs.get(FilterChainCreator.FILTER_NAME_FIRST_DISTANCE);
		Double secondDistance = (Double) distanceArgs.get(FilterChainCreator.FILTER_NAME_SECOND_DISTANCE);
		Double toCompareDistance = (Double) distanceArgs.get(FilterChainCreator.FILTER_NAME_TO_COMPARE_DISTANCE);
		
		boolean canCompareBoth = firstDistance != null && secondDistance != null;
		
		boolean greaterOrEqualThanMinimum = firstDistance != null && toCompareDistance >= firstDistance;
		
		boolean lowerOrEqualThanMaximum = secondDistance != null && toCompareDistance <= secondDistance;
		
		if (canCompareBoth) {
			return greaterOrEqualThanMinimum && lowerOrEqualThanMaximum;
		}
		
		if (firstDistance != null) {
			return greaterOrEqualThanMinimum;
		}
		
		return lowerOrEqualThanMaximum;
		
	}

}
