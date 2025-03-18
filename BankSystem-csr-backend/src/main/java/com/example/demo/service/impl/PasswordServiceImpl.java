package com.example.demo.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.service.PasswordService;

@Service
public class PasswordServiceImpl implements PasswordService {

	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 密碼加密
	
	@Override
	public String encodePassword(String rawPassword) {
		
		String encryptPassword = passwordEncoder.encode(rawPassword);  // 將密碼進行加密
		
		return encryptPassword;
	}
	
	// 密碼驗證

	@Override
	public Boolean verifyPassword(String rawPassword, String encodePassword) {
		
		if( !passwordEncoder.matches(rawPassword, encodePassword) ) {
			throw new PasswordInvalidException("密碼錯誤");
		}
		
		return true ;
	}

	
}
