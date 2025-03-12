package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.aop.check.CheckAdminSession;
import com.example.demo.config.MessageConfig;
import com.example.demo.model.dto.StatisticDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.AccountService;
import com.example.demo.service.GmailService;
import com.example.demo.service.UserService;
import com.google.api.services.gmail.Gmail;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/bank/admin")
@CrossOrigin( origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GmailService gmailService;
	
	@Autowired
	private MessageConfig message;   

	// 用戶管理 ------------------------------------------------------------
  
	@GetMapping("/user-manage")
	private ResponseEntity<ApiResponse<List<UserDto>>> manageAllUser( @RequestParam(required=false) String idNumber ) {
		
		List<UserDto> userDtos;
		
		// 無參數時，回傳所有用戶資料 ( model 無資料 )
		
		if( idNumber == null || idNumber.isEmpty() ) {
			
			userDtos = userService.findAllUsers()   
	                          .stream()
	                          .filter( user-> user.getRole().equals("ROLE_USER"))
	                          .filter( user-> user.getApprove().equals("APPROVED"))
	                          .toList();
		} else {
			
		// 有參數時，回傳指定用戶資料	
			
			userDtos = userService.getUserByIdNumber(idNumber);
		}
			
		return ResponseEntity.ok(ApiResponse.success("Data Fetched",userDtos ));
	}
	
	
	// 變更用戶資料
	
	@GetMapping("/user-manage/update/{id}")
	private String adminUpdatePage(@PathVariable("id") Long userId,Model model) { 
		
		UserDto manageUserDto = userService.getUser(userId);
		
		model.addAttribute("manageUserDto",manageUserDto);
				
		return "admin_manage_update";
	}
	
	
	@PutMapping("/user-manage/update/{id}")
	private ResponseEntity<ApiResponse<String>> updateUser( @RequestBody UserDto manageUserDto) { 
		
		userService.updateUser(manageUserDto);
					                            		
		return ResponseEntity.ok(ApiResponse.success("資料修改成功","用戶ID："+ manageUserDto.getId() ));
	}
	
	
	// 刪除用戶
	
	@PostMapping("/user-manage/remove/{id}")
	private String deleteUser( @PathVariable("id") Long userId) { 

		userService.deleteUser(userId);  // 刪除用戶
  		
		return "redirect:/bank/admin/user-manage";
	}
	
	
	
	// 用戶審核 --------------------------------------------------------------
	
	@GetMapping("/user-approval")
	private ResponseEntity<ApiResponse<List<UserDto>>> getPendingUsers() {
		
		List<UserDto> pendingUserDtos = userService.findAllApprovePendingUsers();
		
		if(pendingUserDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "No Pending User"));
		}
		
		return ResponseEntity.ok(ApiResponse.success("待審核用戶查詢成功", pendingUserDtos)) ;
		
	}	
	
	// 審核用戶
	
	@GetMapping("/user-approval/approve/{id}")
	private ResponseEntity<ApiResponse<String>> approveUserRegister(@PathVariable(value = "id") Long userId) {
		
		// 變更用戶 approve 狀態
		userService.approveUser(userId);
		
		// 發送成功申請郵件，包含已註冊帳號資訊
		UserDto approvedUserDto = userService.getUser(userId);

		try {  	 
			// googleMailServer 寄信
//			Gmail service = gmailService.getGmailService();
//	    	gmailService.sendMessage(service, "me", gmailService.createEmail(approvedUserDto.getEmail(), approvedUserDto.getUsername() + message.getApproveGmailHead() , message.getApproveGmailBody()));  // 使用 @Value 帶入的屬性
		} catch (Exception e) {
	        e.printStackTrace();
	    }
				
		return ResponseEntity.ok(ApiResponse.success( "用戶ID" + userId + "-已審核", "審核成功" ));
	}
	
	
	
	@PostMapping("/user-approval/reject/{id}")
	private ResponseEntity<ApiResponse<String>> rejectUserRegister(@PathVariable(value = "id") Long userId) {
		
		// 發送申請失敗郵件
		UserDto rejectUserDto = userService.getUser(userId);
	
		try {  	 
//			Gmail service = gmailService.getGmailService();
//	    	gmailService.sendMessage(service, "me", gmailService.createEmail(rejectUserDto.getEmail(), rejectUserDto.getUsername() + message.getRejectGmailHead() , message.getRejectGmailBody()));  // 使用 @Value 帶入的屬性
		} catch (Exception e) {
	        e.printStackTrace();
	    }
		
		// 刪除用戶資訊				
		userService.deleteUser(userId);
		
		return ResponseEntity.ok(ApiResponse.success( "用戶ID" + userId + "-已拒絕", "審核不通過" ));
	}
	
	
	
	@GetMapping("/statistics")
	public ResponseEntity<ApiResponse<StatisticDto>> statistics() {
		
		// 計算性別數量
		
		Long maleCount = userService.calcUserQuantityByGender("male");
		
		Long femaleCount = userService.calcUserQuantityByGender("female");
				
		return ResponseEntity.ok(ApiResponse.success( "統計資料查詢成功", new StatisticDto(maleCount,femaleCount)));
	}
	
	
}
