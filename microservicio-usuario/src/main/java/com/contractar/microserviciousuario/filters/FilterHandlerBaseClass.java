package com.contractar.microserviciousuario.filters;

import java.util.Map;
import java.util.Optional;

/**
 * This class sets the chain of responsibility for each available filter. Any extended class must
 * specify the logic to handle its filter in the abstract method defined here. 
 */
public abstract class FilterHandlerBaseClass implements FilterHandler {

	private FilterHandler nextHandler;

	protected FilterHandlerBaseClass() {
	}

	protected FilterHandlerBaseClass(FilterHandler nextHandler) {
		this.nextHandler = nextHandler;
	}

	public FilterHandler getNextHandler() {
		return nextHandler;
	}

	@Override
	public void setNextHanlder(FilterHandler handler) {
		this.nextHandler = handler;
	}

	abstract boolean handleFilter(Map<String, Object> args);

	@Override
	public boolean handleRequest(Map<String, Object> args) {
		boolean areCurrentFiltersOk = handleFilter(args);

		Optional<FilterHandler> nextOpt = Optional.ofNullable(getNextHandler());

		if (areCurrentFiltersOk && nextOpt.isPresent()) {
			return nextOpt.get().handleRequest(args);
		}

		return areCurrentFiltersOk;
	}
}
