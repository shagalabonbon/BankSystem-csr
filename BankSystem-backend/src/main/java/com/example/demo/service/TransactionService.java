package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;

public interface TransactionService {
	
	// 轉帳
	TransactionRecord transfer(String fromAccountNumber,String toAccountNumber,BigDecimal amount,String description) ;
	
	// 換匯
	TransactionRecord exchange(String fromAccountNumber,String toAccountNumber,BigDecimal exchangeRate,BigDecimal amount,String description);
}
	

