package com.example.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
			
	private Long    id;
		
	private String  username;     
	
	private String  gender ;       

	private String  email;        
	
	private String  phone;
	
	private String  role;
	
	private String  approve;    
		
}
