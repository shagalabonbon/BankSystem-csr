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
		
		return ResponseEntity.ok(ApiResponse.success("註冊成功", "註冊成功"));
	}
	
	
	@GetMapping("/foreigns/result")
	public String createResultPage(@RequestParam String unregisterCurrencyCode , HttpSession session,Model model) {
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
		
        AccountDto newForeignAccountDto = accountService.getAccountByCurrencyCode(loginUserDto.getId(),unregisterCurrencyCode);  
		
		model.addAttribute("newForeignAccount",newForeignAccountDto);
			
		return "account_foreign_register_result";

	}
		
	
	// 交易紀錄 -------------------------------------------------------------------
	
	@GetMapping("/{id}/histories")
	public String TxHistoryPage(@PathVariable("id") Long accountId , Model model , HttpSession session) {
		
		// 驗證帳戶 ( 可改成從 session 獲取 )
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
		
		Account account = accountRepository.findByUserIdAndId(loginUserDto.getId(), accountId).orElseThrow( ()->new AccountNotFoundException() );
		
		model.addAttribute("account",account);
		
		// 尋找所有交易紀錄
		
		List<TransactionRecordDto> transactionDtos = transactionRecordService.getAllTransactionHistory(accountId);
		
		model.addAttribute("transactionDtos",transactionDtos);
		
		return "account_tx_history";
		
	}
	
	@GetMapping("/{id}/histories/interval")
	public String intervalTxHistory(@PathVariable("id") Long accountId, @RequestParam String startDate,@RequestParam String endDate,Model model,HttpSession session) {
		
		// 傳遞 account 資料 ( 可改成從 session 獲取 )
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
				
		Account account = accountRepository.findByUserIdAndId(loginUserDto.getId(), accountId).orElseThrow( ()->new AccountNotFoundException() );
				
		model.addAttribute("account",account);
		
		// 傳遞 transactionDtos 資料
		
		List<TransactionRecordDto> chosenTransactionDtos = transactionRecordService.getIntervalTransactionHistory(accountId,startDate,endDate);
		
		model.addAttribute("transactionDtos",chosenTransactionDtos);  // 覆蓋 transactionDtos 以更新前端顯示資料 
		
		return "account_tx_history";
		
	}
	
	@GetMapping("/{id}/histories/top50")
	public String top50TxHistory( @PathVariable("id") Long accountId , Model model,HttpSession session) {
		
		// 傳遞 account 資料 ( 可改成從 session 獲取 )
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
						
		Account account = accountRepository.findByUserIdAndId(loginUserDto.getId(), accountId).orElseThrow( ()->new AccountNotFoundException() );
						
		model.addAttribute("account",account);
		
		// 傳遞 transactionDtos 資料
		
		List<TransactionRecordDto> top50TransactionDtos = transactionRecordService.getTop50TransactionHistory(accountId);
		
		model.addAttribute("transactionDtos",top50TransactionDtos);
		
		return "account_tx_history";
		
	}
	
	
}
