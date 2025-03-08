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

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/bank/account")
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
	private ModelMapper modelMapper;
	
	
	// 創建外幣帳戶
	
	@GetMapping("/foreign-account")
	public String createForeignAccountPage(HttpSession session,Model model) {
		
		// 篩選欲申請外幣帳號
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
			
		List<Currency> unregisterCurrencies = currencyService.getUnregisterCurrencies( loginUserDto.getId() );
		
		model.addAttribute("unregisterCurrencies",unregisterCurrencies);
		
		return "account_foreign_register";
	}
	
	
	@PostMapping("/foreign-account")
	public String createForeignAccount(@RequestParam String unregisterCurrencyCode ,HttpSession session,Model model,RedirectAttributes redirectAttributes) {
		
		// 表單送出申請
		User loginUser = modelMapper.map((UserDto)session.getAttribute("loginUserDto"),User.class);

		accountService.createAccount(loginUser,unregisterCurrencyCode,"1000","87");
		
		redirectAttributes.addAttribute("unregisterCurrencyCode", unregisterCurrencyCode);  // 用於傳遞參數屬性
		
		return "redirect:/bank/account/foreign-account/result";
	}
	
	
	@GetMapping("/foreign-account/result")
	public String createResultPage(@RequestParam String unregisterCurrencyCode , HttpSession session,Model model) {
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto");
		
        Account newForeignAccount = accountService.getAccountByCurrencyCode(loginUserDto.getId(),unregisterCurrencyCode);  
		
		model.addAttribute("newForeignAccount",newForeignAccount);
			
		return "account_foreign_register_result";

	}
	
	
	// ------------------------------------------------------------------
		
	@GetMapping("/all-account")
	public ResponseEntity<ApiResponse<List<Account>>> getAllUserAccounts( @PathVariable Long userId ) {
		
		// 尋找全部帳號
		
		List<Account> accounts = accountService.findAllUserAccounts(userId);
		
		return ResponseEntity.ok(ApiResponse.success( "ID：" + userId + "帳號查詢成功", accounts));
	}
	
	
	
	@GetMapping("/{id}/transaction-history")
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
	
	@GetMapping("/{id}/transaction-history/between")
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
	
	@GetMapping("/{id}/transaction-history/top50")
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
	
	
	// 如果要顯示前端頁面，必須要傳遞所有 Thymeleaf EL ( ${} ) 有使用到的資料，不然無法顯示 
	
	// ex.account_tx_history 有用到 ${account}、${transactionDtos}，每個要在頁面顯示的 controller 必須傳兩個資料，不能有 null
	
	
	
}
