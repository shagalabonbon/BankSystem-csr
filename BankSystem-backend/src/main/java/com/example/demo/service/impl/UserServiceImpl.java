package com.example.demo.service.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.exception.userexception.UserAlreadyExistException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.PasswordService;
import com.example.demo.service.RedisService;
import com.example.demo.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordService passwordService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	// 顯示全部用戶 ( 後台 )

	@Override
	public List<UserDto> findAllUsers() {

		List<User> users = userRepository.findAll();

		List<UserDto> userDtos = users.stream()
				                      .map(user -> modelMapper.map(user, UserDto.class))
				                      .toList();

		return userDtos;
	}
	
	// 註冊用戶 
	
	@Override
	public void register(String username,String idNumber,String password, String gender, String email, String phone) {

		// 檢查用戶是否重複  
			
		if ( userRepository.findByIdNumber(idNumber).isPresent() ) {    
			throw new UserAlreadyExistException();
		}
			
		// 將密碼加鹽
	
		String hashPassword = passwordService.encodePassword(password);
		
		// 輸入資料並註冊
		
		User newUser = new User();
		
		newUser.setUsername(username);
		newUser.setIdNumber(idNumber);
		newUser.setHashPassword(hashPassword);
		newUser.setGender(gender);
		newUser.setEmail(email);
		newUser.setPhone(phone);
		newUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
		newUser.setRole("ROLE_USER");
		newUser.setApprove("PENDING");
			
		userRepository.save(newUser);	
		
	}
	
	
	@Override
	public List<UserDto> findAllApprovePendingUsers() { 

		List<UserDto> pendingUserDtos = userRepository.findByApprove("PENDING")
				                                   .stream()
				                                   .map( u -> modelMapper.map(u, UserDto.class))
				                                   .toList();
		
		return pendingUserDtos;
	}
	
	
	// 審核
	
	@Override
	public void approveUser(Long userId) {
		
        User user = userRepository.findById(userId)
        		                  .orElseThrow(()->new UserNotFoundException());
        
        user.setApprove("APPROVED");
        userRepository.save(user);

        // 審核通過後創建台幣帳戶
        
        accountService.createAccount(user,"TWD","1000","53");
    }
	
	

	@Override
	public UserDto getUser(Long userId) { 
		
		UserDto userDto = userRepository.findById(userId)
				                        .map(user -> modelMapper.map(user, UserDto.class) )
				                        .orElseThrow(()->new UserNotFoundException("用戶ID無效："+userId));  // orElseThrow 在 Optional 有值時會返回非 Optional 的物件
		return userDto;	
	}
	
	
	@Override
	public List<UserDto> getUserByIdNumber(String idNumber) {
		
		List<UserDto> userDtos = userRepository.findByIdNumber(idNumber)
				                               .stream()
				                               .map(user->modelMapper.map(user, UserDto.class))
				                               .toList();
		
		if(userDtos.isEmpty()) {	
			throw new UserNotFoundException();  // 在欄位後方顯示錯誤
		}
		
		return userDtos;
	}
	
	
	@Override
	public UserDto getUserByEmail(String email) {
			
		UserDto userDto = userRepository.findByEmail(email)
					                    .map(user->modelMapper.map(user,UserDto.class))
					                    .orElse(null);                              // 找不到返回 null
		return userDto;
	}
	
	
	

	// 更新資料 -----------------------------------


	@Override
	public void updateUser( UserDto userDto ) {       
		
		// 查詢用戶
		
		Optional<User> optUser = userRepository.findById(userDto.getId());
		
		if (optUser.isEmpty()) {
	        throw new UserNotFoundException("User not found with userId: " + userDto.getId());
	    }
		
		// 變更資料
		
		User user = optUser.get();
		
		if( userDto.getUsername() != null && !userDto.getUsername().trim().isEmpty() ) {     // 更改名稱
			user.setUsername(userDto.getUsername());
		}
		
		if( userDto.getGender() != null && !userDto.getGender().trim().isEmpty() ) {         // 更改性別
			user.setGender(userDto.getGender());
		}
		
		if( userDto.getEmail() != null && !userDto.getEmail().trim().isEmpty()) {            // 更改郵箱
			user.setEmail(userDto.getEmail());	
		}
		
		if( userDto.getPhone() != null && !userDto.getPhone().trim().isEmpty()) {            // 更改電話
			user.setPhone(userDto.getPhone());
		}	
		
		userRepository.save(user);		
		
		
	}
	
	
	// OK

	@Override
	public void updatePassword(Long userId, String oldPassword, String newPassword) {

		// 驗證用戶
		
		User user = userRepository.findById(userId).orElseThrow( ()->new UserNotFoundException() );

		// 驗證舊密碼
		
		Boolean isOldPwCorrect = passwordService.verifyPassword( oldPassword, user.getHashPassword() ); 

		// 設定新密碼
		
		if (isOldPwCorrect) {

			String newEncodePassword = passwordService.encodePassword(newPassword);
            
			user.setHashPassword(newEncodePassword);
			
			userRepository.save(user);  // 更新資料庫資訊 
		}	
	}
	
	
	@Override
	public void updatePassword(Long userId,String newPassword) {

		// 驗證用戶
		
		User user = userRepository.findById(userId).orElseThrow( ()->new UserNotFoundException() );

		// 設定新密碼

		String newEncodePassword = passwordService.encodePassword(newPassword);
            
		user.setHashPassword(newEncodePassword);
			
		userRepository.save(user);  // 更新資料庫資訊 
			
	}
	
	
	
	// -------------------------------------------------
	
	@Override
	public void deleteUser(Long userId) {
		
		userRepository.deleteById(userId);
		
	}
	
	

	@Override
	public void updateRole(Long userId, String role) {
		// TODO Auto-generated method stub
		
	}

	
	// -------------------------------------------------- 
	
	// 統計性別
	
	@Override
	public Long calcUserQuantityByGender(String Gender) {
		
		Long certainGenderAmount = userRepository.findAll()
				                                 .stream()
				                                 .filter(user->user.getGender().equalsIgnoreCase(Gender))
				                                 .count();	
		return certainGenderAmount;
	}
	
	
	// --------------------------------------------------
	
	
	
	
}
