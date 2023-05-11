package com.contractar.microserviciosecurity.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;

@RestController
public class SecurityController {
	 @Autowired
	    private JWKSet jwkSet;

	    @GetMapping("/.well-known/openid-configuration/.well-known/oauth-authorization-server/json/keys.json")
	    public Map<String, Object> keys() {
	        return this.jwkSet.toJSONObject();
	    }
}
