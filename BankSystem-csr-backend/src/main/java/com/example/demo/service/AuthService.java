package com.example.demo.service;

import com.example.demo.model.dto.UserDto;

public interface AuthService {
	
	String login(String idNumber, String password) ;
	
	String adminLogin(String idNumber, String password);
    
    // 產生驗證碼
    String generateAuthCode();
    
    // 核對驗證碼
    Boolean checkAuthCode(String authCodeInput);
    
    
    
    
}
