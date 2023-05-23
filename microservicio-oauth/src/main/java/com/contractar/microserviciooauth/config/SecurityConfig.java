package com.contractar.microserviciooauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.GET, "/oauth/login").anonymous()
				.requestMatchers("/error", "/actuator/**").permitAll()
				.anyRequest().authenticated());
		
		http.oauth2Client(oauth2 -> oauth2.clientRegistrationRepository(this.clientRepository()));
		
		http.oauth2Login().and().formLogin().disable();
		
		http.httpBasic().disable();
	
		return http.build();
	}
	
    private ClientRegistrationRepository clientRepository() {

      ClientRegistration clientRegistration =
    	   ClientRegistration
          .withRegistrationId("contractar")
          .clientSecret(passwordEncoder().encode("contractar"))
          .scope("read", "write")
          .authorizationGrantType(AuthorizationGrantType.PASSWORD)
          .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
          .build();

      return new InMemoryClientRegistrationRepository(clientRegistration);
    }
    
    @Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
