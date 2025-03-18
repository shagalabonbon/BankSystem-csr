package com.example.demo.aop.check;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/* @Before、@After 只能執行邏輯，無法直接阻止目標方法執行
 * 
 * 使用 @Around 執行全面控制
 * 
 * */ 

@Aspect
@Component
public class UserSessionCheckAspect {

    @Autowired
    private HttpServletRequest request;
    
    @Around("@annotation(com.example.demo.aop.check.CheckUserSession)")
    public Object checkUserSession(ProceedingJoinPoint joinPoint) throws Throwable {
    	
        HttpSession session = request.getSession();
        UserDto loginUserDto = (UserDto) session.getAttribute("loginUserDto");

        if (loginUserDto == null) {
            
            return "redirect:/bank/login"; 
        }

        return joinPoint.proceed(); // 正常執行目標方法
    }
}
