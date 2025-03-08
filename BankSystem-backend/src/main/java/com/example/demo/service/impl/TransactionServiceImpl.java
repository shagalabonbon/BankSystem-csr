package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.accountexception.AccountNotFoundException;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRecordRepository;
import com.example.demo.service.TransactionRecordService;
import com.example.demo.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private TransactionRecordService transactionRecordService ;
	
	@Autowired
	private TransactionRecordRepository transactionRecordRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	
	// 轉帳 ( 差錯誤處理 )

	@Override
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		rollbackFor = {RuntimeException.class}
    )
	public TransactionRecord transfer(String fromAccountNumber,String toAccountNumber, BigDecimal amount, String description) {
		
		TransactionRecord transactionRecord = new TransactionRecord();
		
		try {
		
			// 確認用戶
				
		    Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
		                                           .orElseThrow(() -> new AccountNotFoundException("來源帳戶不存在"));
	
		    Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
		                                         .orElseThrow(() -> new AccountNotFoundException("目標帳戶不存在"));
			
			// 驗證金額
		    
		    if( amount.compareTo(BigDecimal.ZERO) <= 0 ) {	         // 轉帳金額需大於 0
		    	throw new RuntimeException("轉帳金額需大於 0");
		    }
		    
		    if( amount.compareTo(fromAccount.getBalance()) > 0 ) {	 // 轉帳金額需小於帳戶餘額
		    	throw new RuntimeException("帳戶餘額不足");
		    }
		    
		    // 進行轉帳
		    
		    BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(amount); 
		    
		    BigDecimal toAccountNewBalance = toAccount.getBalance().add(amount); 
		    
		    fromAccount.setBalance(fromAccountNewBalance);
		    
		    toAccount.setBalance(toAccountNewBalance);
			
		    accountRepository.save(fromAccount);
		    
		    accountRepository.save(toAccount);
		    
			// 創建交易紀錄並設置成功狀態
		    
		    transactionRecord = transactionRecordService.createTransactionRecord( 
		    		            	fromAccount.getAccountNumber(),
		    		                toAccount.getAccountNumber(),
		    		                amount,
		    		                TransactionType.Transfer,
		    		                description );
		    
		    transactionRecord.setStatus(TransactionStatus.Success);
		    
			transactionRecordRepository.save(transactionRecord);  // 儲存交易紀錄
		    
		}catch (Exception e) {
			
			// 錯誤則創建失敗交易紀錄 
			
			transactionRecord.setStatus(TransactionStatus.Failed);
			transactionRecord.setDescription(e.getMessage());
			
			// createFailedTransaction
			
		}
		
		return transactionRecord;
	}

	
	// 換匯 
	@Override
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		rollbackFor = {RuntimeException.class}
	)
	public TransactionRecord exchange(String fromAccountNumber,String toAccountNumber,BigDecimal exchangeRate, BigDecimal amount ,String description) {
		
		TransactionRecord transactionRecord = new TransactionRecord();
		
		try {
			
			// 確認帳戶
			
			Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
	                                               .orElseThrow(() -> new AccountNotFoundException("來源帳戶不存在"));
			
			Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
	                                             .orElseThrow(() -> new AccountNotFoundException("來源帳戶不存在"));
			
			// 驗證金額
		    
		    if( amount.compareTo(BigDecimal.ZERO) <= 0 ) {	         // 轉帳金額需大於 0
		    	throw new RuntimeException("轉帳金額需大於 0");
		    }
		    
		    if( amount.compareTo(fromAccount.getBalance()) > 0 ) {	 // 轉帳金額需小於帳戶餘額
		    	throw new RuntimeException("帳戶餘額不足");
		    }
			
			// 進行換匯 ( 賣出 )
		     	
			BigDecimal exchangeAmount = amount.divide(exchangeRate,2, RoundingMode.HALF_UP);  // 限制結果精度並使用四捨五入 ( 不然會出現循環小數錯誤 )
					
			fromAccount.setBalance( fromAccount.getBalance().subtract(amount) );   // 台幣帳戶減少
			
			toAccount.setBalance( toAccount.getBalance().add(exchangeAmount) );    // 外幣帳戶增加
			
			accountRepository.save(fromAccount);
			
			accountRepository.save(toAccount);
			
			// 創建交易紀錄並設置成功狀態
		    
		    transactionRecord = transactionRecordService.createTransactionRecord( 
		    						fromAccount.getAccountNumber(),
		    						toAccount.getAccountNumber(),
		    		                amount,
		    		                TransactionType.SellExchange,
		    		                description
		    		            );
		    
		    transactionRecord.setStatus(TransactionStatus.Success);
		    
		    transactionRecordRepository.save(transactionRecord);   // 儲存交易紀錄
						
		}catch (Exception e) {
			
			
			// 設置失敗狀態並記錄異常
	        transactionRecord.setStatus(TransactionStatus.Failed);
	        transactionRecord.setDescription(e.getMessage());

	        // 儲存失敗的交易紀錄
	        transactionRecordRepository.save(transactionRecord);  // 儲存失敗紀錄

	        // 重新拋出異常以確保事務回滾
	        throw new RuntimeException("交易處理失敗: " + e.getMessage(), e);
			
//			// 傳遞到交易失敗頁面
//			
//			
//			// 儲存失敗紀錄
//			
//			transactionRecord.setStatus(TransactionStatus.Failed);
//			transactionRecord.setDescription(e.getMessage());
//			
//			// throw new RuntimeException();  // 避免捕捉例外導致事務不回滾
				
		}
		
		return transactionRecord;
	}



}
