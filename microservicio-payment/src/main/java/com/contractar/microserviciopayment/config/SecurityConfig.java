package com.contractar.microserviciopayment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    /**
     * Configuración general para entorno de desarrollo
     * (permite todo y desactiva cualquier autenticación o CORS restrictivo)
     */
    @Bean
    @Profile("dev")
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * Configuración para entorno de producción:
     * permite todas las rutas sin login,
     * pero aplica CORS limitado en /actuator/prometheus
     */
    @Bean
    @Profile("prod")
    public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * CORS específico para Prometheus (solo en prod)
     */
    @Bean
    @Profile("prod")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
            "http://localhost:9090",
            "https://prometheus.contratar.com.ar",
            "https://grafana.contratar.com.ar"
        ));

        configuration.setAllowedMethods(List.of("GET"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/actuator/prometheus", configuration);

        return source;
    }
}
