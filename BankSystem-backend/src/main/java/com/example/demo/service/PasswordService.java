package com.example.demo.service;

// BCrypt 密碼加鹽 ( SHA-256 )

public interface PasswordService {
	
	// 密碼加鹽
	String  encodePassword (String rawPassword);   
	
	// 密碼驗證
	Boolean verifyPassword (String rawPassword, String encodePassword); 
	
	
	
}
