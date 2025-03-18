package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.entity.Currency;

public interface CurrencyService {

	List<Currency> getAllCurrencies(); 
	
	Currency getCurrencyByCode(String code);
	
	List<Currency> getUnregisterCurrencies(Long userId);
}
