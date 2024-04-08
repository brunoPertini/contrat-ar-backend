package com.contractar.microserviciousuario.filters;

import java.util.Map;

public class PriceFilterHandler extends FilterHandlerBaseClass {

	public PriceFilterHandler() {}

	public PriceFilterHandler(FilterHandler nextHandler) {
		super(nextHandler);
	}

	@Override
	boolean handleFilter(Map<String, Object> args) {
		Map<String, Integer> priceArgs = (Map<String, Integer>) args.get(FilterChainCreator.PRICE_FILTERS);
		
		Integer firstPrice = (Integer) priceArgs.get(FilterChainCreator.PRICE_FILTERS_FIRST_PRICE);
		Integer secondPrice = (Integer) priceArgs.get(FilterChainCreator.PRICE_FILTERS_SECOND_PRICE);
		Integer toComparePrice = (Integer) priceArgs.get(FilterChainCreator.FILTER_NAME_TO_COMPARE_PRICE);
		
		boolean canCompareBoth = firstPrice != null && secondPrice != null;
		
		boolean greaterOrEqualThanMinimum = firstPrice != null && toComparePrice >= firstPrice;
		
		boolean lowerOrEqualThanMaximum = secondPrice != null && toComparePrice <= secondPrice;
		
		if (canCompareBoth) {
			return greaterOrEqualThanMinimum && lowerOrEqualThanMaximum;
		}
		
		if (firstPrice != null) {
			return greaterOrEqualThanMinimum;
		}
		
		return lowerOrEqualThanMaximum;
		
	}

}
