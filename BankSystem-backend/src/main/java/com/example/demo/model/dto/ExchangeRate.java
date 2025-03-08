package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.example.demo.model.entity.Currency;

import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
	
	private String currencyName;
	
    private String currencyCode;
	
	private BigDecimal spotBuy;
	
	private BigDecimal spotSell;
	
	private BigDecimal cashBuy;
	
	private BigDecimal cashSell;
	
	private String     renewTime;

}
