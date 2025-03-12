package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.AccountDto;
import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/bank")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class HomeController {
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserService userService;
	
	// ----------------------------------------------------------------
	
	@GetMapping("/index")
	public String indexPage(Model model) {
		
		ExchangeRate USexchangeRate = exchangeRateService.getTargetExchangeRate("USD");
		ExchangeRate EUexchangeRate = exchangeRateService.getTargetExchangeRate("EUR");
		ExchangeRate JPexchangeRate = exchangeRateService.getTargetExchangeRate("JPY");
		ExchangeRate CNexchangeRate = exchangeRateService.getTargetExchangeRate("CNY");
		
		model.addAttribute("USexchangeRate",USexchangeRate);
		model.addAttribute("EUexchangeRate",EUexchangeRate);
		model.addAttribute("JPexchangeRate",JPexchangeRate);
		model.addAttribute("CNexchangeRate",CNexchangeRate);
		
		return "index";  
	} 
	
	// ----------------------------------------------------------------
	
	
	@GetMapping("/home")
	public String homePage(HttpSession session, Model model) {
		
		UserDto loginUserDto = (UserDto) session.getAttribute("loginUserDto");   // 檢查有沒有登入時存入的 Dto 資料
		
		if( loginUserDto != null) {                           
			
			model.addAttribute("loginUserDto",loginUserDto);  // 將 Dto 傳遞到頁面
			
			// 尋找全部帳號，並用 model 傳遞
			
			List<AccountDto> accountDtos = accountService.findAllUserAccounts(loginUserDto.getId());
			
			model.addAttribute("accounts",accountDtos);
			
			return "user_homepage";
		}
		
		return "redirect:/bank/login";  // 尚未登入，返回登入頁面
	}
	
	
	@GetMapping("/admin/home")
	public String adminHomePage(HttpSession session, Model model) {
		
		UserDto loginAdminDto = (UserDto)session.getAttribute("loginAdminDto");   // session 檢查登入時資料
		
		if( loginAdminDto == null) {                           
			
			return "redirect:/bank/admin/login";  // 尚未登入，返回登入頁面
		}
		
		// 回傳所有用戶資料
		
		List<UserDto> allUserDtos = userService.findAllUsers()   
	                             			   .stream()
	                             			   .filter( user-> user.getRole().equals("ROLE_USER"))
	                             			   .filter( user-> user.getApprove().equals("APPROVED"))
	                             			   .toList();    
	    				                             
		model.addAttribute("allUserDtos",allUserDtos);
		
		model.addAttribute("loginAdminDto",loginAdminDto);  // 資料用 model 傳遞到頁面
		
		System.out.print(allUserDtos);
			
		return "admin_homepage";
		
	}
	
	
	
	
	
	
}
