package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table( name = "transaction_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecord {                // 交易紀錄
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String fromAccountNumber;           // 來源帳戶

	private String toAccountNumber;             // 目標帳戶
	
	private BigDecimal amount;                  // 金額

	private Timestamp transactionTime;          // 交易時間
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;    // 交易類型 ( 轉帳、存款、取款 )
	
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;           // 交易狀態 ( 成功、失敗、待處理 )
	
	private String description;                 // 交易備註 
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	
	/* id：交易的唯一標識符。
	fromAccountId：來源帳戶的ID。
	toAccountId：目標帳戶的ID。
	amount：交易金額。
	transactionType：交易類型（例如，轉帳、存款、取款）。
	timestamp：交易時間。
	status：交易狀態（例如，成功、失敗、待處理等）。
	description：交易描述（例如，"支付給商店"）。
	*/
}
