package com.example.demo.service;

public interface RedisService {
	
	// 儲存資料 
	void saveData(String key, String value);
	
	// 提取資料
    public String getData(String key);

    // 刪除資料
    public void deleteData(String key);

    
    
}
