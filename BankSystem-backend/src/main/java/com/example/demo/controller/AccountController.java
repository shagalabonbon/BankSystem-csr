package com.example.demo.controller;


import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exception.accountexception.AccountNotFoundException;
import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Currency;
import com.example.demo.model.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.CurrencyService;
import com.example.demo.service.TransactionRecordService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

/* 帳號系統
 * 
 * 管理所有帳號：/accounts
 * 管理外匯帳號：/accounts/foreigns
 * 申請外匯帳號：/accounts/foreigns
 * 查詢交易紀錄：/accounts/{id}/histories
 * 
 * 最近50筆紀錄：/accounts/{id}/histories/top50
 * 指定區間紀錄：/accounts/{id}/histories/interval
 * 
 * */


@RestController
@RequestMapping("/bank/accounts")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private TransactionRecordService transactionRecordService;
	
	@Autowired
	private CurrencyService currencyService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;
	
		
	@GetMapping("")
	public ResponseEntity<ApiResponse<List<AccountDto>>> getAllUserAccounts( @RequestParam Long userId ) {
		
		// 尋找全部帳號
		
		List<AccountDto> accountDtos = accountService.findAllUserAccounts(userId);
		
		System.out.print(accountDtos);
		
		return ResponseEntity.ok(ApiResponse.success( "ID：" + userId + "帳號查詢成功", accountDtos));
	}
	
	// 外幣帳戶 -----------------------------------------------------
	
	@GetMapping("/foreigns")
	public ResponseEntity<ApiResponse<List<Currency>>> createForeignAccountPage( @RequestParam(value = "id") Long userId ) {
		
		// 篩選欲申請外幣帳號
			
		List<Currency> unregisterCurrencies = currencyService.getUnregisterCurrencies( userId );
		
		return ResponseEntity.ok(ApiResponse.success("未註冊貨幣查詢成功", unregisterCurrencies));
	}
	
	
	@PostMapping("/foreigns")
	public ResponseEntity<ApiResponse<String>> createForeignAccount(@RequestBody AccountDto accountDto ) {
		
		UserDto loginUserDto   = userService.getUser(accountDto.getUserDto().getId());
		
		User    loginUser      = modelMapper.map(loginUserDto, User.class); 
		
		String  unregisterCode = accountDto.getCurrency().getCode();
		
		accountService.createAccount(loginUser,unregisterCode,"1000","87");
		
		return ResponseEntity.ok(ApiResponse.success("帳號申請成功", "帳號申請成功"));
	}
	
		
	
	// 交易紀錄 -------------------------------------------------------------------
	
	@GetMapping("/{id}/histories")
	public ResponseEntity<ApiResponse<List<TransactionRecordDto>>> TxHistoryPage(@PathVariable("id") Long accountId ) {
				
		// 尋找所有交易紀錄
		
		List<TransactionRecordDto> transactionDtos = transactionRecordService.getAllTransactionHistory(accountId);
		
		
		return ResponseEntity.ok(ApiResponse.success("交易紀錄查詢成功", transactionDtos));
		
	}
	
	
}
