package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.currencyexception.CurrencyNotFoundException;
import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Currency;
import com.example.demo.repository.CurrencyRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.CurrencyService;

@Service
public class CurrencyServiceImpl implements CurrencyService {

	@Autowired
	private CurrencyRepository currencyRepository;
	
	@Autowired
	private AccountService accountService;
	
	
	@Override
	public List<Currency> getAllCurrencies() {
		
		List<Currency> currencies = currencyRepository.findAll();
		
		return currencies;
	}


	@Override
	public Currency getCurrencyByCode(String code) {
		
		Currency currency = currencyRepository.findByCode(code).orElseThrow(()->new CurrencyNotFoundException());
		
		return currency;
	}


	@Override
	public List<Currency> getUnregisterCurrencies(Long userId) {  // 未註冊的貨幣表單
		
		// 取得所有貨幣清單
	    List<Currency> allCurrencies = currencyRepository.findAll();
	    
	    // 取得用戶已註冊帳戶的貨幣清單
	    List<Currency> registeredCurrencies = accountService.findAllUserAccounts(userId).stream()
	                                                        .map(Account::getCurrency)
	                                                        .toList();
	    
	    // 篩選出未註冊的貨幣清單
	    List<Currency> unregisteredCurrencies = allCurrencies.stream()
	                                                         .filter(currency -> !registeredCurrencies.contains(currency))
	                                                         .toList();
	    
	    return unregisteredCurrencies;
				                                             
	}
	
	
	
	
	
	
	
	
	
	

}
