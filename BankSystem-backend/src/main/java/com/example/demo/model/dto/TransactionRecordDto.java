package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordDto {
	
    private Long   id;
	
	private String fromAccountNumber;          // 來源帳戶ID
	
	private String toAccountNumber;            // 目標帳戶ID
	
	private BigDecimal amount;                 // 金額
	
	private Timestamp  transactionTime;        // 交易時間
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;   // 交易類型 ( 轉帳、存款、取款 )
	
	@Enumerated(EnumType.STRING)
    private TransactionStatus status;          // 交易狀態 ( 成功、失敗、待處理 )
	
	private String description;                // 交易備註
}
