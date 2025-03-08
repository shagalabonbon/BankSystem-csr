package com.example.demo.service;

import java.security.Key;

import com.example.demo.model.dto.UserDto;

import io.jsonwebtoken.Claims;

public interface JwtService {
	
	Key    getSecretKey();
	
	String generateJwt(Long userId);
	
	Claims parseJwt(String token);

}
