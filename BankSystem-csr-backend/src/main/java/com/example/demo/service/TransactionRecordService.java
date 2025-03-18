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

public interface TransactionRecordService {
	
	// 建立交易紀錄
	TransactionRecord createTransactionRecord(String fromAccountNumber,String toAccountNumber,BigDecimal amount,TransactionType transactionType,String description);     
	
	// 查詢前50筆交易歷史
	List<TransactionRecordDto> getTop50TransactionHistory(Long accountId); 
	
	// 查詢所有帳戶交易紀錄
	List<TransactionRecordDto> getAllTransactionHistory(Long accountId);
	
	// 查詢區間交易歷史
	List<TransactionRecordDto> getIntervalTransactionHistory(Long accountId,String startDate,String endDate);  
	
}
	

