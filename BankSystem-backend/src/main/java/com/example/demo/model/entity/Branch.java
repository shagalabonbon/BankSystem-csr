package com.example.demo.model.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {   // 分行
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;             // 分行名稱

	private String code;             // 分行代碼 ( 4碼 )
	
	private String location;         // 分行地址
	
	@OneToMany(mappedBy = "branch")	
	private List<Account> accounts;  // 分行帳號總覽
	
}
