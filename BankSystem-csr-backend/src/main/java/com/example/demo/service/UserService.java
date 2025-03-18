package com.example.demo.service;

import java.util.List;

import com.example.demo.exception.branchexception.BranchNotFoundException;
import com.example.demo.exception.currencyexception.CurrencyNotFoundException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;

public interface UserService {
	
	// 全用戶 ( 後台 )
	List<UserDto> findAllUsers(); 
	
	// 尋找未審核用戶
	
	List<UserDto> findAllApprovePendingUsers();
	
	// 註冊 
	void register(String username,String idNumber,String password,String gender,String email,String phone);   
    
	// 尋找單一用戶	
	
	UserDto getUser(Long userId);   
	
	UserDto getUserByEmail(String email);

	List<UserDto> getUserByIdNumber(String idNumber);
	
	
	
	// 更新資料 
	void updateUser(UserDto userDto);  
	
	// 變更密碼
	void updatePassword(Long userId,String oldPassword,String newPassword);
	
	void updatePassword(Long userId,String newPassword);  // 忘記密碼用
	
	// 變更權限
	void updateRole(Long userId,String role);
	
	// 刪除用戶
	void deleteUser(Long userId); 
	
	// 變更審核狀態
	void approveUser(Long userId);
	
	// 計算用戶數 (依性別) 
	Long calcUserQuantityByGender(String Gender);
	

	
}
