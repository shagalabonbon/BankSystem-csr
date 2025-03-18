package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.RedisService;

@SpringBootTest
public class TestRedisService {
	
	@Autowired
	private RedisService redisService;
	
	@Test
	public void test() {
		
		redisService.saveData("1", "User");
		
		String data = redisService.getData("1");
		
		System.out.println(data);
	}
}
