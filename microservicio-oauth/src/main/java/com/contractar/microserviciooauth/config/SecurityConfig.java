package com.contractar.microserviciooauth.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private RSAPublicKey storePublicKey;

	@Autowired
	private RSAPrivateKey storePrivateKey;
	
	@Value("${spring.security.oauth2.client.id}")
	private String clientId;
	
	@Value("${spring.security.oauth2.client.secret}")
	private String clientSecret;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		
		http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/error",
				"/actuator/**",
				"/oauth/login").anonymous()
				.anyRequest().authenticated());
		
		http.oauth2Client(oauth2 -> oauth2.clientRegistrationRepository(this.clientRepository()));
		
		http.oauth2Login().authorizationEndpoint()
        .baseUri("/oauth/login");
		
		http.formLogin().disable();	
		
		http.httpBasic().disable();
	
		return http.build();
	}
	
	@Bean
    public ClientRegistrationRepository clientRepository() {

      ClientRegistration clientRegistration =
    	   ClientRegistration
          .withRegistrationId(clientId)
          .clientSecret(passwordEncoder().encode(clientSecret))
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
    
	@Bean
	public JwtDecoder jwtDecoder() throws JOSEException {
		RSAPublicKey publicKey = storePublicKey;
		RSAPrivateKey privateKey = storePrivateKey;

		RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).build();

		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
	}
}
