package com.contractar.microserviciogateway.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {
	@Override
     public void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/usuarios/**").permitAll()
        .anyRequest().authenticated();
        
        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        
        http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
