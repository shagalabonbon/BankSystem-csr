package com.example.demo.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	// 儲存資料
	public void saveData(String key, String value) {		
        redisTemplate.opsForValue().set(key, value, 5, TimeUnit.MINUTES);     // 有效期 5 分鐘
	}
	
	// 讀取資料
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);    
    }
       
	// 刪除資料
	@Override
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}

	
}
