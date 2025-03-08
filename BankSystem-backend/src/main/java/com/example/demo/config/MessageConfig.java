package com.example.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;

@Configuration
@PropertySource(value="classpath:properties/message_zh_TW.properties", encoding = "UTF-8")  // 必須指定編碼為 UTF-8 ( 預設是 ISO-8859-1 )
@Getter
public class MessageConfig { 
	
	// 當.properties 檔案放置於自訂位置時 ( 非直接在 src 資料夾下 )  
	
	// 需要創建一個 Configuration，使用 PropertySource 在 Spring 啟動前加載需要的資料
	
	// 在要使用的類別下創建屬性，並使用 @Value 將資料帶入屬性
	
	@Value("${gmail.head.approve.success}")   // @Value 尋找 .properties 文件訊息，並帶入屬性
	private String approveGmailHead;
	
	@Value("${gmail.body.approve.success}")
	private String approveGmailBody;
	
	@Value("${gmail.head.approve.failed}")   // @Value 尋找 .properties 文件訊息，並帶入屬性
	private String rejectGmailHead;
	
	@Value("${gmail.body.approve.failed}")
	private String rejectGmailBody;
	
	@Value("${gmail.head.password.reset}")
	private String resetGmailHead;
	
	@Value("${gmail.body.password.reset}")
	private String resetGmailBody;
	
//	@Value("${error.message.UserNotFoundException}")
//	private String userNotFound; 
//	
//	@Value("${error.message.UserAlreadyExistException}")
//	private String userAlreadyExist; 
//	
//	@Value("${error.message.AccountNotFoundException}")
//	private String accountNotFound;
//	
//	@Value("${error.message.BranchNotFoundException}")
//	private String branchNotFound;
//	
//	@Value("${error.message.CurrencyNotFoundException}")
//	private String currencyNotFound;
//	
//	@Value("${error.message.PasswordInvalidException}")
//	private String passwordInvalid;
//	
//	@Value("${error.message.UnauthorizedException}")
//	private String unauthorized;
//	
//	@Value("${error.message.InsufficientFundsException}")
//	private String insufficientFunds;
	
	
}
