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
import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.dto.ExchangeDto;
import com.example.demo.model.dto.ExchangePageDto;
import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.dto.TransferDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


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
	
    // 轉帳 ( 建立一個  )
	
	@GetMapping("/transfer")               
	public ResponseEntity<ApiResponse<List<AccountDto>>> transferPage( @RequestParam(value="id") Long userId ) {	
		
		List<AccountDto> twdAccountDtos = accountService.findAllUserTWDAccounts( userId );
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", twdAccountDtos));            
	}
	
	
	@PostMapping("/transfer")
	public ResponseEntity<ApiResponse<TransactionRecordDto>> doTransfer(@RequestBody TransferDto transferDto) {
		
		TransactionRecordDto transferRecordDto =  transactionService.transfer(transferDto.getFromAccountNumber(),transferDto.getToAccountNumber(),transferDto.getAmount(),transferDto.getDescription());
		
		return ResponseEntity.ok(ApiResponse.success("轉帳成功",transferRecordDto ));
	}
	
	
	// 換匯  --------------------------------------

	@GetMapping("/exchange")
	public ResponseEntity<ApiResponse<ExchangePageDto>> exchangePage(@RequestParam(value="id") Long userId) { 	
		
		// 頁面資料
		
		List<AccountDto> twdAccountDtos = accountService.findAllUserTWDAccounts(userId);
		
		List<AccountDto> foreignAccountDtos = accountService.findAllUserForeignAccounts(userId);
		
		List<ExchangeRate> foreignExchangeRates = exchangeRateService.getAllForeignAccountExchangeRate(userId);	
		
		ExchangePageDto exchangeDto = new ExchangePageDto(twdAccountDtos,foreignAccountDtos,foreignExchangeRates);	
		
		System.out.print(foreignAccountDtos);
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功",exchangeDto));
	}


	@PostMapping("/exchange") 
	public ResponseEntity<ApiResponse<TransactionRecordDto>> doExchange(@RequestBody ExchangeDto exchangeDto) {
		
		// 進行換匯 ***
		
		TransactionRecordDto exchangeRecordDto = transactionService.exchange(exchangeDto.getFromAccountNumber(),exchangeDto.getToAccountNumber(),exchangeDto.getExchangeRate(),exchangeDto.getAmount(),exchangeDto.getDescription());
		
		return ResponseEntity.ok(ApiResponse.success("換匯成功",exchangeRecordDto ));  // 回傳交易資料 TransactionRecordDto
	}
		
	
	// 匯率爬蟲 ---------------------------------------------------------------
	
	@GetMapping("/exchange-rate")
	public ResponseEntity<ApiResponse<List<ExchangeRate>>> getAllExchangeRates() {
		
		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
		
		return ResponseEntity.ok( ApiResponse.success( "匯率查詢成功", exchangeRates ) );
	}
	
	
}
