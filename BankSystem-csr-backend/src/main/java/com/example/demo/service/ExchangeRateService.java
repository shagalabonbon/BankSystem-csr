package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.ExchangeRate;

public interface ExchangeRateService {
	
	public List<ExchangeRate> getTwdExchangeRate();
	
	public List<ExchangeRate> getAllForeignAccountExchangeRate(Long userId);
	
	public ExchangeRate getTargetExchangeRate(String currencyCode);
	
	
	
}
