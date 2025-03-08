package com.example.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.aop.check.CheckUserSession;
import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;


/* 
 * 請求方法      URL 路徑                                  功能                                                      
 * ---------------------------------------------------
 * 	 GET     /transaction/txHistory/{accountId}           查詢帳號交易紀錄     
 *   POST    /transaction/transfer                        轉帳
 *   POST    /transaction/exchange                        換匯
        
     顯示於： 
  
 * */

@RestController
@RequestMapping("/bank/transaction")
@CrossOrigin(origins = "http://localhost:5173" , allowCredentials = "true")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private AccountService accountService;
	

	@GetMapping("/transfer")               // 轉帳頁面
	@CheckUserSession
	public String transferPage(Model model,HttpSession session) {
		
		UserDto loginUserDto =  (UserDto)session.getAttribute("loginUserDto");
		
		List<Account> twdAccounts = accountService.findAllUserTWDAccounts(loginUserDto.getId());
		
		model.addAttribute("transferDto", new TransactionRecordDto()); // 當有使用 th:object，就必須要有已存入 model 的物件、或創建初始化的物件傳遞 
		
		model.addAttribute("twdAccounts", twdAccounts);
		
		return "transfer";            
	}
	
	@PostMapping("/transfer/check")
	@CheckUserSession
	public String checkTransfer(@ModelAttribute TransactionRecordDto transferDto,Model model ) {
		
		model.addAttribute("transferDto",transferDto); // 將輸入資料傳遞到確認頁
		
		return "transfer_check";
	}
	
	
	@PostMapping("/transfer/confirm")
	@CheckUserSession
	public String doTransfer( @ModelAttribute TransactionRecordDto transferDto,Model model) {
		
		transactionService.transfer(transferDto.getFromAccountNumber(),transferDto.getToAccountNumber(),transferDto.getAmount(),transferDto.getDescription());
		
		model.addAttribute("transferDto",transferDto);
		
		return "transfer_result"; 
	}
	
	
	// 換匯  --------------------------------------

	@GetMapping("/exchange")
	@CheckUserSession
	public String exchangePage(Model model,HttpSession session) { 
		
		// 執行匯率更新
		
		UserDto loginUserDto =  (UserDto)session.getAttribute("loginUserDto");
		
		List<Account> twdAccounts = accountService.findAllUserTWDAccounts(loginUserDto.getId());
		
		List<ExchangeRate> foreignExchangeRates = exchangeRateService.getAllForeignAccountExchangeRate(loginUserDto.getId());
		
		model.addAttribute("exchangeDto",new TransactionRecordDto()); // 傳遞初始化物件  
		
		model.addAttribute("twdAccounts",twdAccounts);  
			
		model.addAttribute("foreignExchangeRates",foreignExchangeRates);  
		
		return "exchange";
	}
	
	
	@PostMapping("/exchange/check")
	@CheckUserSession
	public String doExchange( @ModelAttribute TransactionRecordDto exchangeDto,@RequestParam String currencyCode,@RequestParam String targetRate,Model model,HttpSession session) {
		
		UserDto loginUserDto =  (UserDto)session.getAttribute("loginUserDto");
		
		String formatAmount =  String.format("%.2f",exchangeDto.getAmount().divide(new BigDecimal(targetRate),0, RoundingMode.HALF_UP).doubleValue());  // String.format 需要傳入 double、float 來顯示數字，BigDecimal 需要轉換 
		
		String toAccountNumber = accountService.getAccountByCurrencyCode(loginUserDto.getId(),currencyCode).getAccountNumber();
		
		exchangeDto.setToAccountNumber(toAccountNumber);
		
		model.addAttribute("exchangeDto",exchangeDto);
		
		model.addAttribute("targetRate",targetRate);
		
		model.addAttribute("formatAmount",formatAmount);  // 外幣金額
			
		return "exchange_check";
	}
	
	
	
	@PostMapping("/exchange/confirm") 
	@CheckUserSession
	public String checkExchange(@ModelAttribute TransactionRecordDto exchangeDto,@RequestParam String targetRate,@RequestParam String formatAmount,Model model) {
		
		// 進行換匯 ***
		
		transactionService.exchange(exchangeDto.getFromAccountNumber(),exchangeDto.getToAccountNumber(),new BigDecimal(targetRate),exchangeDto.getAmount(),exchangeDto.getDescription());
		
		model.addAttribute("exchangeDto",exchangeDto);
		
        model.addAttribute("targetRate",targetRate);
		
		model.addAttribute("formatAmount",formatAmount);
		
		// 有錯誤則顯示錯誤頁面
		
		return "exchange_result";
	}
	
	
	
	
	
	/* 匯率爬蟲 - 完成 */
	
//	@GetMapping("/exchange-rate")
//	public String exchangeRatePage(Model model,HttpSession session) {
//		
//		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
//		
//		// 確保列表非空，並提取第一個 ExchangeRate 的更新時間
//		
//	    String renewTime = exchangeRates.get(0).getRenewTime();
//		
//		model.addAttribute("exchangeRates",exchangeRates);
//		
//		model.addAttribute("renewTime",renewTime);
//		
//		// 存入 session 以便登入期間取用 ( 測試非必須 )
//		
//		session.setAttribute("exchangeRates",exchangeRates);
//		
//		return "exchange_rate";
//		
//	}
	
	
	@GetMapping("/exchange-rate")
	public ResponseEntity<ApiResponse<List<ExchangeRate>>> getAllExchangeRates() {
		
		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
		
		return ResponseEntity.ok( ApiResponse.success( "匯率查詢成功", exchangeRates ) );
	}
	
	
}
