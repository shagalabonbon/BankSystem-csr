package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.mapper.JsonMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.UserService;

@SpringBootTest
public class TestJsonMapper {
	
	@Autowired 
	private JsonMapper jsonMapper;
	
	@Autowired
	private UserService userService;
	
	@Test
	public void testMapper() {
		
		UserDto userDto = userService.getUser(1L);
		
		String json = jsonMapper.toJson(userDto);
		
		System.out.println(json);
		
		UserDto userDto1 = jsonMapper.toObject(json, UserDto.class);
		
		System.out.println(userDto1);
		
	}
	
	
}
