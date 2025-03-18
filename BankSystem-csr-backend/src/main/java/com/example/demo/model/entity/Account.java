package com.example.demo.model.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.AccountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {   

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;                  
	
	private String accountNumber ;    // 帳戶號碼
	
	private BigDecimal balance ;      // 帳戶餘額
	
	private Timestamp  createTime;    // 開戶時間

	@Enumerated(EnumType.STRING)
	private AccountStatus status;     // 帳戶的狀態（例如，啟用、禁用、凍結等）。
	
	@Enumerated(EnumType.STRING)
	private AccountType accountType;  // 帳戶的類型（儲蓄 .. ）。
	
	// 關聯實體 
	
	@ManyToOne
	private Currency currency;
	
	@ManyToOne
	private User user;                // User 可以有多個帳戶  
	
	@ManyToOne
	private Branch branch;
	
	@OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true )  // 刪除帳戶時會一併刪除交易記錄 ( 只影響 )
	private List<TransactionRecord> transactions;
} 
