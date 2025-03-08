package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();  // 使用 BCrypt 演算法加密 ( 密碼加鹽 )
	}
	
	
	// 使用 HttpSecurity 配置請求授權規則
	
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http       
        	.authorizeHttpRequests( authorizeHttpRequests ->
            	authorizeHttpRequests
            		.requestMatchers("/**")
            		.permitAll()
//            	    .requestMatchers("/bank/index","/bank/login","/bank/admin/login")     // 這些 URL 不需要登錄
//                	.permitAll() 
//                	.requestMatchers("/picture/**", "/css/**", "/js/**")                  // 允許靜態資源訪問
//                	.permitAll()            	
//                	.anyRequest()                                                         // 其他請求需要登錄
//                 	.authenticated()                             
	        )
//        	
//	        .formLogin(formLogin ->                              // 使用表單登錄功能來驗證用戶
//	            formLogin
//	                .loginPage("/bank/login")                    // 自定義登錄頁面
//	                .loginProcessingUrl("/bank/login")           // 處理登錄請求的 URL ( security 自動處裡，需要複寫 UserDetailsService )
//	                .defaultSuccessUrl("/bank/user/home", true)  // 登錄成功後的預設頁面
//	                .failureUrl("/bank/login?error=true")        // 登錄失敗後的頁面          ****** 待處理
//	                .permitAll()                                 // 不須授權
//	        )
//	        .logout(logout -> 
//	            logout
//	            	.logoutUrl("/bank/logout")                   // 處理登出請求的 URL
//	            	.logoutSuccessUrl("/bank/index")             // 登出成功後的頁面
//	            	.invalidateHttpSession(true)                 // 清除 session
//	            	.permitAll()                                 // 允許所有請求
//	        )
        	
        	.csrf(csrf -> csrf.disable());
        
        	return http.build();         // http.build() 建構配置
    }

	
}
