package com.contractar.microserviciocommons.helpers;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public final class DatesHelper {
    @Autowired
    private MessageSource messageSource;
    
	private Locale locale;
	
	@Autowired
	public DatesHelper() {
		this.locale = new Locale("es", "AR");
	}
	
	public DatesHelper(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public String getMonthAndYearPattern() {
		return  messageSource.getMessage("date.format.month.and.year", null, locale);
	}
	
	public String getFullDatePattern() {
		return  messageSource.getMessage("date.format.default", null, locale);
	}
}
