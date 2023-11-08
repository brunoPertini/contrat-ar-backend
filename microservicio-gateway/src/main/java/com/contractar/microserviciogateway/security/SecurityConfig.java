package com.contractar.microserviciogateway.security;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private RSAPublicKey storePublicKey;

	@Autowired
	private RSAPrivateKey storePrivateKey;

	@Autowired
	private OAuth2WebSecurityExpressionHandler expressionHandler;
	
	private final Map<String,String> acceptedOrigins = Map.of(
			"dev",
			"http://localhost:3000",
			"prod", 
			 ""
			);
	
	private final String[] vendiblesUrls = {"/vendible/**", "/usuarios/proveedor/**/vendible/**"};
	
	private final String[] productosUrls = {"/product/**"};
	
	private final String[] servicesUrls = {"/service/**"};
	
	private final String[] proveedorUrls = {"/proveedor/**"};

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(new KeyPair(storePublicKey, storePrivateKey));

		return converter;
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withPublicKey(storePublicKey).build();
	}

	@Bean
	public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(
			ApplicationContext applicationContext) {
		OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
		expressionHandler.setApplicationContext(applicationContext);
		return expressionHandler;
	}
	
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
	    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
	    grantedAuthoritiesConverter.setAuthorityPrefix("");

	    final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
	    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
	    return jwtAuthenticationConverter;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors().configurationSource(request -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin(acceptedOrigins.get("dev"));
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        });
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/actuator/**", "/error").permitAll()
				.antMatchers("/oauth/login", "/oauth/public_key")
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.POST, "/usuarios/**")
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(vendiblesUrls).access("@securityUtils.hasValidClientId(request) and isAuthenticated()")
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority("PROVEEDOR_PRODUCTOS", "PROVEEDOR_SERVICIOS", "CLIENTE")
				.antMatchers(HttpMethod.GET, vendiblesUrls[0]).hasAnyAuthority("PROVEEDOR_PRODUCTOS", "PROVEEDOR_SERVICIOS", "CLIENTE")
				.antMatchers(productosUrls).hasAuthority("PROVEEDOR_PRODUCTOS") //TODO: ver porque rompe si no pongo el harcodeo
				.antMatchers(HttpMethod.POST, servicesUrls).hasAuthority("PROVEEDOR_SERVICIOS")
				.antMatchers(HttpMethod.POST,vendiblesUrls).hasAnyAuthority("PROVEEDOR_PRODUCTOS", "PROVEEDOR_SERVICIOS")
				.antMatchers(proveedorUrls).hasAnyAuthority("PROVEEDOR_PRODUCTOS", "PROVEEDOR_SERVICIOS")
				.anyRequest()
				.access("@securityUtils.hasValidClientId(request) and isAuthenticated()");

		http.oauth2ResourceServer(oauth2 -> {
	        oauth2.jwt()
	          .jwtAuthenticationConverter(jwtAuthenticationConverter());
	    });

		http.httpBasic().disable();

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore());
		resources.expressionHandler(expressionHandler);
	}
}