package com.contractar.microserviciousuario.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.contractar.microserviciocommons.infra.SecurityHelper;
import com.contractar.microserviciocommons.constants.RolesNames.RolesValues;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
public class SecurityConfig {

	@Autowired
	private SecurityHelper securityHelper;
	
	private final String[] allowedDevOrigins = {"http://localhost:3000", "http://localhost:8090"};

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
	public JwtDecoder jwtDecoder() throws Exception {
		final String pKey = securityHelper.fetchPublicKey();
		return NimbusJwtDecoder.withPublicKey(securityHelper.getRSAPublicKeyFromString(pKey)).build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		String adminRole = RolesValues.ADMIN.name();
		
		final String [] personalDataUrls = { AdminControllerUrls.ADMIN_USUARIOS_BY_ID,
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
								.requestMatchers(HttpMethod.PATCH, personalDataUrls)
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.DELETE, AdminControllerUrls.ADMIN_USUARIOS_BY_ID, AdminControllerUrls.CHANGE_REQUEST_BY_ID )
								.hasAuthority(adminRole)
								.requestMatchers(HttpMethod.GET, AdminControllerUrls.ADMIN_USUARIOS_SENSIBLE_INFO)
								.hasAuthority(adminRole)
								.anyRequest().permitAll())
				.oauth2ResourceServer(oauth2 -> oauth2.jwt());

		return http.build();
	}
}
