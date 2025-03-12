package com.example.demo.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
	
	private String     fromAccountNumber;   // 轉出帳戶
	
	private String     toAccountNumber;     // 轉入帳戶
		
	private BigDecimal amount;              // 金額
	
	private String     description;         // 備註
		

}
