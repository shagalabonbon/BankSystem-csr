package com.example.demo.service.impl;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.mapper.JsonMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import com.example.demo.service.PasswordService;
import com.example.demo.service.RedisService;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
		
	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private JsonMapper jsonMapper;
	
	@Override
	public String login( String idNumber, String password ) {
		
		// 尋找用戶
		User loginUser = userRepository.findByIdNumber(idNumber)
				                       .orElseThrow( ()->new UserNotFoundException());	
		
		// 驗證密碼
		if(!passwordService.verifyPassword(password,loginUser.getHashPassword())){
			throw new PasswordInvalidException();
		}
		
		// 生成 Token
		String jwt = jwtService.generateJwt(loginUser.getId()); 
		
		// Redis 儲存用戶資料		
		UserDto loginUserDto = modelMapper.map(loginUser, UserDto.class); 		
		String  jsonData     = jsonMapper.toJson(loginUserDto);
		
		redisService.saveData("UserID:"+loginUser.getId(),jsonData);
		
		return jwt ;   // 傳遞 JWT 
		
	}
	
	
	@Override
	public String adminLogin(String idNumber, String password) {
		
		// 驗證用戶
		User loginUser = userRepository.findByIdNumber(idNumber)
				                       .orElseThrow( ()->new UserNotFoundException("用戶不存在") );
		
		// 驗證密碼
		if(!passwordService.verifyPassword(password,loginUser.getHashPassword())){
			throw new PasswordInvalidException("密碼錯誤");
		}
		
		// 驗證權限
		if(!loginUser.getRole().equals("ROLE_ADMIN")) {
			throw new UnauthorizedException();  
		}
		
		// Redis 儲存用戶資料		
		UserDto loginUserDto = modelMapper.map(loginUser, UserDto.class); 		
		String  jsonData     = jsonMapper.toJson(loginUserDto);
		redisService.saveData("UserID:"+loginUser.getId(),jsonData);
		
		String jwt = jwtService.generateJwt(loginUser.getId());
		
		System.out.print(jwt);
		
		return jwt; 
		
	}
	
	
	
	

	// 產生驗證碼 + 驗證 ( 忘記密碼 )

	@Override
	public String generateAuthCode() {
		
		SecureRandom sr = new SecureRandom(); 
		
		Integer authCode = sr.nextInt(900000) + 100000;  // 產生 [100000 ~ 1000000) 之間的隨機數
		
		redisService.saveData("AuthCode", String.valueOf(authCode));
		
		return String.valueOf(authCode);
	
	}


	@Override
	public Boolean checkAuthCode(String authCodeInput) {
		
		String authCode = redisService.getData("AuthCode");
		
		return authCode.equals(authCodeInput);
	}	
	
	
	
	
	
	
	
	
	
	

}
