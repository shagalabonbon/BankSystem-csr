package com.example.demo.aop.check;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Aspect 
@Component
public class AdminSessionCheckAspect {

	@Autowired
    private HttpServletRequest request;
    
    @Around("@annotation(com.example.demo.aop.check.CheckAdminSession)")
    public Object checkUserSession(ProceedingJoinPoint joinPoint) throws Throwable {
    	
        HttpSession session = request.getSession();
        UserDto loginAdminDto = (UserDto) session.getAttribute("loginAdminDto");

        if (loginAdminDto == null) {
            
            return "redirect:/bank/admin/login"; 
        }

        return joinPoint.proceed(); // 正常執行目標方法
    }
	
}
