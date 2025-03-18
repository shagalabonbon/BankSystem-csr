package com.example.demo.model.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;    // 用 jakarta.persistence 才不會存入資料庫
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)  // ID 遞增
	private Long      id;        
	  
	private String    username;        // 用戶名稱
	
	private String    idNumber;        // 身分證字號   
	 
	private String    gender ;         // 性別
	
	private String    hashPassword;    // 密碼加鹽  
	
	@Transient 
	private String    rawPassword;     // 未加密資料 ( 前端註冊綁定用 - @Transient 不存入資料庫 )
	
	private String    email;           // Email    
	
	private String    phone;           // 手機
	
	private Timestamp createTime;      // 註冊時間
	
	private String    role;            // 權限 
	
	private String    approve;         // 審核
    
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)     
    private List<Account> accounts;    // 帳戶列表
	 
}
