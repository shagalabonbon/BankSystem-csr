package com.example.demo.aop.check;              // 自定義註解位置

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target( ElementType.METHOD )              // 註解適用範圍 ( 僅適用於方法 )
@Retention(RetentionPolicy.RUNTIME)        // 註解作用時期 ( 系統運行時持續有效 )
public @interface CheckAdminSession {
	
}
