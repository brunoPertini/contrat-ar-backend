package com.contractar.microservicioconfig.i18n.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import jakarta.annotation.PostConstruct;

@Configuration
public class MessageConfig {
	
	@Value("${default-locale}")
	private String defaultLocale;

	private Locale locale;

	@PostConstruct
	public void init() {
		locale = Locale.forLanguageTag(defaultLocale);
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.forLanguageTag(defaultLocale));
        
        return messageSource;
    }
}
