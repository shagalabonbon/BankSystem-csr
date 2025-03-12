package com.example.demo.service.impl;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.model.dto.UserDto;
import com.example.demo.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;





@Service
public class JwtServiceImpl implements JwtService {
	
	@Value("${jwt.secretKey}")   // 密鑰保存於 application.property
	private String secret; 
	
    private static final Long expireTime = 1000 * 60 * 30L  ;  // 預設 30 分鐘 
    
    
  
	@Override
	public SecretKey getSecretKey() {
	
		SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());       // HMAC-SHA256 演算法，密鑰應該至少為 256 位（即 32 字節）
		
		return secretKey;
	}
	
	
	@Override
	public String generateJwt(Long userId) {  
			
		Date issueAt = new Date();
		Date exp     = new Date( issueAt.getTime() + expireTime ) ; 
				
		JwtBuilder jwtBuilder = Jwts.builder()
									.issuer("YCBank")                       // 簽發人
									.subject(userId.toString())             // 簽發對象 ( 通常放 userID )                      
									.issuedAt(issueAt)                      // 簽發時間
									.expiration(exp)                        // 過期時間
//									.claim("userDetail",userDto )           // 自訂內容 ( private claims )
									
									.signWith(getSecretKey())               // 簽名密鑰
									.id(UUID.randomUUID().toString());      // JWT ID
								
		return jwtBuilder.compact();
	}


	@Override
	public Claims parseJwt(String token) throws JwtException {	
		
		// 解析過程中有錯誤會拋出 JwtException 子類別
		
		return Jwts.parser()
				   .verifyWith(getSecretKey()) 
				   .build()
				   .parseSignedClaims(token)     // 解析 JWS ( 有簽章的JWT )
				   .getPayload();                 		   
		
	}
	
	
	
	
	

}
