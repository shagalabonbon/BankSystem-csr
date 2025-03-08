package com.example.demo;

import java.security.MessageDigest;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.JwtService;

import io.jsonwebtoken.Claims;

@SpringBootTest
public class TestJwtService {
	
	@Value("${jwt.secretKey}")   // 密鑰保存於 application.property
	private String secret; 
	
	@Autowired
	private JwtService jwtService;
	
	@Test
	public void testJwt() {
		
		System.out.println(secret);                  // 密鑰
		
		String jwt = jwtService.generateJwt(1L);     // 產生 JWT
		
		System.out.println(jwt);
		
		Claims payload = jwtService.parseJwt(jwt);   // 解析 JWT
		
		System.out.println(payload);
		
	}
}
