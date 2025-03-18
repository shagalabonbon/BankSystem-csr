package com.example.demo.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.aop.check.CheckAdminSession;
import com.example.demo.aop.check.CheckUserSession;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.mapper.JsonMapper;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.RedisService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/* 使用者
 * 
 * 請求方法      URL 路徑                功能                                                      
 * ---------------------------------------------------
	 GET     /user                      查詢全部 
     GET     /user/get?username=admin   查詢單筆
     POST    /user/register             新增單筆
     POST    /user/update?userId=1      修改單筆
     GET     /user/delete?userId=1      刪除單筆
 * 
 *  @RequestParam 對應 name 屬性
 * 
 * 
 * */ 


@RestController
@RequestMapping("/bank/user")
@CrossOrigin(origins = "http://localhost:5173",allowCredentials = "true")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private JsonMapper jsonMapper;
	

	// 註冊服務 ------------------------------------------
		
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDto>> register( @RequestBody User user ){  // @RequestBody 用於將前端傳遞過來的資料 ( JSON ) 轉為 Java 物件
		
		userService.register( user.getUsername(),
				              user.getIdNumber(),
				              user.getHashPassword(),   // 尚未加密的密碼
				              user.getGender(),
				              user.getEmail(),
				              user.getPhone()
	    );
		
		UserDto userDto = modelMapper.map(user, UserDto.class);
		
		return ResponseEntity.ok( ApiResponse.success("註冊成功", userDto ));
	} 
	
	
	// 使用者更新服務 ----------------------------------------------    
	
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<UserDto>> updateUserDetail( @RequestBody UserDto loginUserDto ) throws UserNotFoundException{
		
	    userService.updateUser( loginUserDto );
	    
		return ResponseEntity.ok( ApiResponse.success( "使用者 ID："+ loginUserDto.getId() +"資料更新成功", loginUserDto) );              
	}
	
	
	// ---------------------------------------
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse<String>> deleteTargetUser( @PathVariable Long userId ){
		
		userService.deleteUser(userId);
		
		return ResponseEntity.ok( ApiResponse.success("已刪除帳號 ID："+ userId , "成功" ));
		
	}
	
	
	// ----------------------------------------
	
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> getUserDetail(@PathVariable Long userId){
		
		// 先查 Redis 有無資料
		
		String redisData = redisService.getData( "UserID:"+userId );
		
		if( redisData!=null ) {
			
			UserDto userDto = jsonMapper.toObject(redisData, UserDto.class);
			
			return ResponseEntity.ok( ApiResponse.success("用戶查詢成功 ( ID："+ userId +")", userDto ));
			
		}
		
		System.out.print(userId);
		
		// Redis 無資料，從資料庫提取
		
		UserDto userDto = userService.getUser(userId);
		
		String jsonData = jsonMapper.toJson(userDto);
		
		redisService.saveData("UserID:"+userId, jsonData);
		
		return ResponseEntity.ok( ApiResponse.success("用戶查詢成功 ( ID："+ userId +")", userDto ));
	}
	
	
}
