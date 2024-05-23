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

import com.contractar.microserviciogateway.constants.RolesNames.RolesValues;

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
		
	private final String[] proveedorUrls = {"/proveedor/**"};
	
	private final String[] clientesUrls = {"/cliente/**"};
	
	private final String[] adminUrls = {"/admin/**", "/admin/change-requests", "/admin/usuarios/proveedor/**"};

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
		
		String proveedorProductoRole = RolesValues.PROVEEDOR_PRODUCTOS.name();
		String clienteRole = RolesValues.CLIENTE.name();
		String proveedorServicioRole = RolesValues.PROVEEDOR_SERVICIOS.name();
		String adminRole = RolesValues.ADMIN.name();
		
		String vendiblesOperationsAccsesRule = "@securityUtils.userIdsMatch(request, \"proveedor\") and hasAnyAuthority('" + proveedorProductoRole +
				"','" + proveedorServicioRole + "','" + adminRole+ "')";
		
		String clientesOperationsAccsesRule = "@securityUtils.userIdsMatch(request, \"cliente\") and hasAnyAuthority('" + clienteRole + "','" + adminRole+ "')";

		
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/actuator/**", "/error", "/geo/**").permitAll()
				.antMatchers("/oauth/login", "/oauth/public_key", "/oauth/userId")
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.POST, "/usuarios/**") // Registro de usuarios
				.access("@securityUtils.hasValidClientId(request)")
				.antMatchers(HttpMethod.PUT, adminUrls[2]).hasAnyAuthority(proveedorServicioRole, proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.PUT, adminUrls[0]).hasAuthority(adminRole)
				.antMatchers(HttpMethod.PATCH, adminUrls[0]).hasAuthority(adminRole)
				.antMatchers(HttpMethod.GET, adminUrls[1]).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.POST, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.POST, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule)	
				.antMatchers(HttpMethod.PUT, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule) 
				.antMatchers(HttpMethod.DELETE, vendiblesUrls[1]) .access(vendiblesOperationsAccsesRule)
				.antMatchers(HttpMethod.GET, vendiblesUrls[0]).hasAnyAuthority(proveedorProductoRole, proveedorServicioRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.GET, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, clienteRole, adminRole)
				.antMatchers(HttpMethod.POST, productosUrls[0]).hasAnyAuthority(proveedorProductoRole, adminRole)
				.antMatchers(HttpMethod.PUT, clientesUrls).access(clientesOperationsAccsesRule)
				.antMatchers(proveedorUrls).access(vendiblesOperationsAccsesRule)
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