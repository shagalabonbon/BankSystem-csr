package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.UserService;

@SpringBootTest
public class TestUserService {
	
    @Autowired
    private UserService userService;
	
	@Test
	public void test() {
		
		// getUser - OK
		
		try {
			
			UserDto userDto = userService.getUser(1L);
			
			System.out.println(userDto);	
			
		}catch (Exception e) {
			
			System.out.println( "錯誤：" + e.getMessage() );
		}
		
		// updatePassword - OK
		
		try {
			
			userService.updatePassword(1L,"","");	
			
		}catch (Exception e) {
			
			System.out.println( "錯誤：" + e.getMessage() );
		}
		
		
		
		
		
	}
}
