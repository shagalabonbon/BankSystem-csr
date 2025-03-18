package com.example.demo.exception;


import java.net.http.HttpRequest;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.MessageConfig;
import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserAlreadyExistException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

// 自訂例外可決定是否繼承受檢例外 ( ex.Exception )
// 也可繼承非受檢例外 ( ex.RuntimrException )，可簡潔代碼，減少顯式撰寫 throws，但拋出錯誤時仍需要處裡

// 當例外未處理時最終會由 JVM 進行錯誤堆疊 ( Stack Trace )

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private MessageSource messageSource;       // 提取 .property 資訊，並提供多國語言支持
	
	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	// 記錄錯誤
	public void logError(Exception ex) {
		
		String errorMessage = String.format(
			    "Exception: %s\nOccurred in: %s.%s (line %d)\nMessage: %s",
			    ex.getClass().getName(),                                  // 錯誤的類型
			    ex.getStackTrace()[0].getClassName(),                     // 發生錯誤的類別名稱
			    ex.getStackTrace()[0].getMethodName(),                    // 發生錯誤的方法名稱
			    ex.getStackTrace()[0].getLineNumber(),                    // 發生錯誤的行號
			    ex.getMessage()                                           // 錯誤消息
			    );
		
		logger.error(errorMessage);
		
		/*  輸出
		 	Exception: java.lang.NullPointerException
		 	Occurred in: com.example.service.UserService.getUserById (line 42)
		 	Message: Cannot invoke "Object.toString()" because "object" is null
		*/
	}
	
	
	@ExceptionHandler( Exception.class )
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex, Locale locale) {
        
		String     message = "";
		HttpStatus status  = null;
		
		// 記錄錯誤
		logError(ex);
		
		// ApiResponse.error 提供錯誤碼及錯誤狀態	
		if( ex instanceof PasswordInvalidException ){				
			message = messageSource.getMessage("exception.password_invalid", null, locale);	
			status = HttpStatus.UNAUTHORIZED;              // Unauthorized (401) - 身份驗證問題，需要提供正確的用戶名和密碼或其他憑證
		}
		
		if( ex instanceof UserNotFoundException ){			
			message = messageSource.getMessage("exception.user_not_found", null, locale);	
			status = HttpStatus.NOT_FOUND; 
		}
		
		if( ex instanceof UserAlreadyExistException ){			
			message = messageSource.getMessage("exception.user_already_exist", null, locale);		
			status = HttpStatus.CONFLICT;                   // Conflict (409) - 請求與伺服器當前狀態衝突 
		}
		
		if( ex instanceof InsufficientFundsException ){			
			message = messageSource.getMessage("exception.insufficient_funds", null, locale);		
			status = HttpStatus.FORBIDDEN; 
		}
		
		if( ex instanceof UnauthorizedException ){			
			message = messageSource.getMessage("exception.unauthorized", null, locale);		
			status = HttpStatus.FORBIDDEN;                  // Forbidden (403) - 授權問題，即便用戶身份驗證成功，但用戶的權限不夠，無法執行該操作
		}

		
		ApiResponse<String> response = ApiResponse.error( status.value(),message);
		
		return ResponseEntity.status(status).body(response) ;            
		
    }	
	
}
