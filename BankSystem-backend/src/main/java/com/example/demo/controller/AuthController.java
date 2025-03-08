package com.example.demo.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.MessageConfig;
import com.example.demo.model.dto.LoginDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AuthService;
import com.example.demo.service.GmailService;
import com.example.demo.service.UserService;
import com.google.api.services.gmail.Gmail;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/bank/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private UserService userService;

	@Autowired
	private GmailService gmailService;

	@Autowired
	private MessageConfig message;
	
	@Autowired
	private ModelMapper modelMapper;
	
	// 登入服務 

	@PostMapping("/userlogin")
	public ResponseEntity<ApiResponse<String>> login( @RequestBody LoginDto loginDto ) {

		// 登入獲取 JWT
		String jwt = authService.login( loginDto.getIdNumber(), loginDto.getPassword() );

		return ResponseEntity.ok( ApiResponse.success("登入成功",jwt) );
		
	}
	
	@PostMapping("/adminlogin")
	public ResponseEntity<ApiResponse<String>> adminLogin( @RequestBody LoginDto loginDto ) {

		// 登入獲取 JWT
		String jwt = authService.adminLogin( loginDto.getIdNumber(), loginDto.getPassword() );

		return ResponseEntity.ok( ApiResponse.success("登入成功",jwt) );
		
	}


	@PostMapping("/logout")
	public String logout(HttpSession session) {

		session.invalidate();

		return "redirect:/bank/index";

	}


	// 忘記密碼 -----------------------------------------------------------

	@GetMapping("/user/recovery")
	private String recoveryPage() {

		return "user_recovery";
	}

	@GetMapping("/user/recovery/continue")
	private String checkCodePage() {

		return "user_recovery_verify";
	}

	@GetMapping("/user/recovery/reset")
	private String resetPage() {

		return "user_recovery_reset";
	}

	@GetMapping("/user/recovery/result")
	private String recoveryResult() {

		return "user_recovery_result";
	}

	@PostMapping("/user/recovery")
	public String sendRecoveryMail(@RequestParam String email, RedirectAttributes redirectAttributes,
			HttpSession session) {

		UserDto userDto = userService.getUserByEmail(email); // 找不到會拋出 null

		if (userDto == null) {

			redirectAttributes.addAttribute("errorMessage", "Email 不存在");

			return "redirect:/bank/user/recovery";
		}

		// 傳遞使用者資料

		session.setAttribute("userDto", userDto);

		// 發送驗證碼郵件

		try {
			Gmail service = gmailService.getGmailService();
//			gmailService.sendMessage(service, "me", gmailService.createEmail(email, message.getResetGmailHead(),
//					message.getResetGmailBody() + authService.generateAuthCode())); // 使用 @Value 帶入的屬性
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/bank/user/recovery/continue";
	}

	@PostMapping("/user/recovery/continue")
	public String checkAuthCode(@RequestParam String authCode, RedirectAttributes redirectAttributes,
			HttpSession session) {

		if (!authService.checkAuthCode(authCode)) {

			redirectAttributes.addAttribute("errorMessage", "驗證碼輸入錯誤");

			return "redirect：/bank/user/recovery/continue";
		}

		return "redirect:/bank/user/recovery/reset";
	}

	@PostMapping("/user/recovery/reset")
	public String resetPassword(@RequestParam String newPassword, HttpSession session) {

		UserDto userDto = (UserDto) session.getAttribute("userDto");

		userService.updatePassword(userDto.getId(), newPassword);

		return "redirect:/bank/user/recovery/result";
	}

	// 註冊服務 ------------------------------------------

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDto>> register( @RequestBody User user ) { // @RequestBody 將前端資料 (JSON) 轉為 Java 物件

		userService.register( user.getUsername(), 
							  user.getIdNumber(), 
							  user.getRawPassword(),    // 尚未加密的密碼
							  user.getGender(), 
							  user.getEmail(), 
							  user.getPhone()
		);

		UserDto userDto = modelMapper.map(user, UserDto.class);

		return ResponseEntity.ok(ApiResponse.success("註冊成功", userDto));
	}

}
