package com.contractar.microserviciousuario.config;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.http.HttpMethod;
import com.contractar.microserviciocommons.constants.controllers.AdminControllerUrls;
import com.contractar.microserviciocommons.constants.controllers.PromotionControllerUrls;
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {
	
	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	
	private final String[] allowedDevOrigins = {"http://localhost:3000", frontendUrl};

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		final JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	public JwtDecoder jwtDecoder(SecurityHelper securityHelper) throws IllegalArgumentException, NoSuchAlgorithmException, InvalidKeySpecException {
		final String pKey = securityHelper.fetchPublicKey();
		return NimbusJwtDecoder.withPublicKey(securityHelper.getRSAPublicKeyFromString(pKey)).build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		String adminRole = RolesValues.ADMIN.name();
		String proveedorProductoRole = RolesValues.PROVEEDOR_PRODUCTOS.name();
		String proveedorServicioRole = RolesValues.PROVEEDOR_SERVICIOS.name();
		
		final String [] onlyAdminUrls = { AdminControllerUrls.ADMIN_USUARIOS_BY_ID,
				AdminControllerUrls.ADMIN_PROVEEDORES_BY_ID,
				AdminControllerUrls.CHANGE_REQUEST_BY_ID};
		
		http.cors().configurationSource(request -> {
			CorsConfiguration corsConfiguration = new CorsConfiguration();
			corsConfiguration.setAllowedOrigins(Arrays.asList(allowedDevOrigins));
			
			 // TODO: AGREGAR CHEQUEO DE AMBIENTE DEV
            String origin = request.getHeader("Origin");
            if (origin != null && origin.contains("ngrok-free.app")) {
                corsConfiguration.addAllowedOrigin(origin);
            }
            
            
			corsConfiguration.addAllowedMethod("*");
			corsConfiguration.addAllowedHeader("*");
			corsConfiguration.setAllowCredentials(false);
			return corsConfiguration;
		});

		http.csrf().disable()
				.authorizeHttpRequests(
						authorize -> authorize.requestMatchers(HttpMethod.POST, AdminControllerUrls.USUARIOS_BASE_URL)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.PATCH, onlyAdminUrls)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.PUT, AdminControllerUrls.ADMIN_USUARIOS_ACTIVE)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.DELETE, AdminControllerUrls.ADMIN_USUARIOS_BY_ID)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.DELETE, AdminControllerUrls.CHANGE_REQUEST_BY_ID)
								.hasAnyAuthority(adminRole, proveedorProductoRole, proveedorServicioRole)
								.requestMatchers(HttpMethod.GET, AdminControllerUrls.ADMIN_USUARIOS_SENSIBLE_INFO)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.POST, PromotionControllerUrls.PROMOTION_INSTANCE_BASE_URL)
								.hasAnyAuthority(adminRole, proveedorProductoRole, proveedorServicioRole)
								.anyRequest().permitAll())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt());

		return http.build();
	}
}
