package com.contractar.microserviciogateway.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableResourceServer
public class SecurityConfig extends ResourceServerConfigurerAdapter {	
	@Override
     public void configure(HttpSecurity http) throws Exception {
    	http.csrf().disable();
    	http.authorizeRequests().antMatchers(HttpMethod.POST, "/usuarios/**").permitAll();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/usuarios/**",
        "/oauth/login",
        "/actuator/**").permitAll()
        .antMatchers("/error").permitAll()
        .anyRequest().authenticated();
        
        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
                
        http.httpBasic().disable();
        
        http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
