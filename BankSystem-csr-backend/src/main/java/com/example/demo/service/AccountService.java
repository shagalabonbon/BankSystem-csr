package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.exception.accountexception.AccountNotFoundException;
import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.exception.branchexception.BranchNotFoundException;
import com.example.demo.exception.currencyexception.CurrencyNotFoundException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Branch;
import com.example.demo.model.entity.Currency;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.model.entity.User;


// 帳戶服務

public interface AccountService {
	
	// 產生帳戶號碼
	String generateAccountNumber(Branch branch,String businessCode);
	
	// 建立帳號 (台幣、外幣)
	void createAccount(User user,String curCode,String branchCode,String businessCode);
 
	// 尋找指定用戶全部帳號
	List<AccountDto> findAllUserAccounts(Long userId); 
	
	List<AccountDto> findAllUserTWDAccounts(Long userId);
	
	List<AccountDto> findAllUserForeignAccounts(Long userId);
	
	
	// 尋找指定貨幣帳號 
    AccountDto getAccountByCurrencyCode(Long userId,String currencyCode);
		
	// 獲取貨幣帳號總額
	BigDecimal calcTotalBalance(UserDto userDto,Currency currency);
	
	
	
	
	
	 

}
