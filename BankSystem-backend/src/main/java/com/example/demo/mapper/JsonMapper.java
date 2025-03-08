package com.example.demo.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JsonMapper {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	// 轉 JSON
	
	public String toJson(Object object) {		
		try {				
			return objectMapper.writeValueAsString(object);  // 轉換為 JSON 字串
		}catch(JsonProcessingException ex) {
			log.error("JSON 轉換失敗，物件：{}，異常訊息：{}", object, ex.getMessage(), ex);
			return "";
		}		
	};
	
	// 轉物件
	
    public <T> T toObject(String json,Class<T> targetClass)  {
    	
    	// 檢查傳入參數
    	if (json == null || json.isEmpty()) {
    		return null;
    	}
    	
    	// 轉換為 JSON 字串
    	try {				
			return objectMapper.readValue(json,targetClass);  
		}catch(JsonProcessingException ex) {
			log.error("物件轉換失敗，Json：{}，異常訊息：{}", json, ex.getMessage());
			return null;
		}
	};
	
}
