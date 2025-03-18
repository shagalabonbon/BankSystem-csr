package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeDto {
	
	// 交易用
	
	private String     fromAccountNumber;   // 換匯帳戶
	
	private String     toAccountNumber;     // 外幣帳戶
	
	private BigDecimal amount;              // 換匯金額
	
	private BigDecimal exchangeRate;        // 交易匯率
	
	private String     currencyCode;        // 交易貨幣代碼
	
	private String     description;         // 備註
	
}
