package com.contractar.microserviciogateway.security;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

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

import com.contractar.microserviciogateway.constants.RolesNames.RolesValues;
import com.contractar.microserviciocommons.constants.CustomHeaders;
import com.contractar.microserviciocommons.constants.controllers.ImagenesControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.UsersControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.VendiblesControllersUrls;

@Configuration
public class SecurityConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private RSAPublicKey storePublicKey;

	@Autowired
	private RSAPrivateKey storePrivateKey;

	@Autowired
	private OAuth2WebSecurityExpressionHandler expressionHandler;
	
	private final List<String> acceptedOrigins = List.of("http://contractar-frontend:3000", "https://contratar.com.ar", "http://localhost:3000", "https://a5412afb6b59.ngrok-free.app");
	
	private final String[] vendiblesUrls = {"/vendible/**", "/usuarios/proveedor/**/vendible/**"};
	
	private final String[] productosUrls = {"/product/**"};
		
	private final String[] proveedorUrls = {"/proveedor/**"};
	
	private final String[] clientesUrls = {"/cliente/**"};
	
	private final String[] adminUrls = {"/admin/**", "/admin/change-requests", "/admin/usuarios/proveedor/**"};
	
	private final String[] signupEmailUrls = {"/mail/signup/link", "/mail/signup/ok"};
	
	private final String[] passwordEmailUrls = {"/mail/password/forgot", "/security/token"};
	
	private final String[] emailServicePublicUrls = {"/mail/contact"};
	
	private final String[] staticContentPublicUrls = {"/static/document/**"};
	
	private final String[] publicPayUrls = {"/pay/**"};
	
	private final String webHookUrl = "/pay/notification/**";
	
	private final String promotionBaseUrl = "/promotion";

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
            acceptedOrigins.stream().forEach(url -> corsConfiguration.addAllowedOrigin(url));
            
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.setAllowCredentials(false);
            corsConfiguration.addExposedHeader(CustomHeaders.ACCOUNT_STATUS);
            return corsConfiguration;
        });
		
		String proveedorProductoRole = RolesValues.PROVEEDOR_PRODUCTOS.name();
		String clienteRole = RolesValues.CLIENTE.name();
		String proveedorServicioRole = RolesValues.PROVEEDOR_SERVICIOS.name();
		String adminRole = RolesValues.ADMIN.name();
		
		final String authenticatedAndWithHeadersAccess = "@securityUtils.hasValidClientId(request) and isAuthenticated()";
		
		final String isAdminUserAccess = "@securityUtils.isAdminUser(request)";
		
		String vendiblesOperationsAccsesRule = "hasAuthority('" + adminRole+ "') or @securityUtils.userIdsMatch(request, \"proveedor\") and hasAnyAuthority('" + proveedorProductoRole +
				"','" + proveedorServicioRole + "')";
		
		String clientesOperationsAccsesRule = "hasAuthority('" + adminRole+ "') or @securityUtils.userIdsMatch(request, \"cliente\") and hasAnyAuthority('" + clienteRole + "')";

		
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/actuator/**", "/error", "/geo/**", webHookUrl).permitAll()
				.antMatchers(emailServicePublicUrls).permitAll()
				.antMatchers(HttpMethod.GET, "/plan").permitAll()
				.antMatchers("/oauth/login", "/oauth/public_key", "/oauth/userId", signupEmailUrls[0], signupEmailUrls[1], staticContentPublicUrls[0])
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.POST, "/usuarios/**", ImagenesControllerUrls.UPLOAD_PROVEEDOR_PHOTO_BY_DNI_URL) // Registro de usuarios
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.POST, passwordEmailUrls[0])
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.POST, adminUrls[1]).hasAnyAuthority(proveedorServicioRole, proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.PUT, adminUrls[2]).hasAnyAuthority(proveedorServicioRole, proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.PATCH, adminUrls[0]).hasAuthority(adminRole)
				.antMatchers(HttpMethod.GET, adminUrls[1]).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.POST, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.POST, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule)	
				.antMatchers(HttpMethod.PUT, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule) 
				.antMatchers(HttpMethod.DELETE, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule)
				.antMatchers(HttpMethod.DELETE, VendiblesControllersUrls.DELETE_VENDIBLE).hasAuthority(adminRole)
				.antMatchers(HttpMethod.GET, vendiblesUrls[0]).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.POST, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.PUT, clientesUrls).access(clientesOperationsAccsesRule)
				.antMatchers(proveedorUrls).access(vendiblesOperationsAccsesRule)
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.GET, publicPayUrls[0]).permitAll() // This is to let pass payment check in signup stage. TODO: do it through a better approach
				.antMatchers(publicPayUrls).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, adminRole)
				.antMatchers(HttpMethod.GET, passwordEmailUrls[1]).access("@securityUtils.tokenContainsType(request) and @securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.GET, UsersControllerUrls.GET_USUARIO_INFO).access("@securityUtils.isAdminUser(request) or @securityUtils.userIdsMatch(request, \"usuarios\")")
				.antMatchers(HttpMethod.GET, promotionBaseUrl).permitAll()
				.antMatchers(HttpMethod.POST, promotionBaseUrl).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, adminRole)
				.antMatchers(HttpMethod.GET, PromotionControllerUrls.PROMOTION_INSTANCE_BY_ID).access(authenticatedAndWithHeadersAccess +
						" and @securityUtils.userIdsMatch(request, \"promotion\") or"
						+ isAdminUserAccess)
				.anyRequest().access(authenticatedAndWithHeadersAccess);

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