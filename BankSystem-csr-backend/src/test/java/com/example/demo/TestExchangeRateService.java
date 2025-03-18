package com.example.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.service.ExchangeRateService;

@SpringBootTest
public class TestExchangeRateService {
	
    @Autowired
    private ExchangeRateService exchangeRateService;
	
	@Test
	public void test() {
		
		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
		
		System.out.println(exchangeRates);
		
	}
}
