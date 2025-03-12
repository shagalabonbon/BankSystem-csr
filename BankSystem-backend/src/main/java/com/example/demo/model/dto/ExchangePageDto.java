package com.example.demo.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePageDto {
	
	// 頁面用資料
	
	private List<AccountDto> twdAccountDtos;
	
	private List<AccountDto> foreignAccountDtos;
		
	private List<ExchangeRate> exchangeRates;
}
