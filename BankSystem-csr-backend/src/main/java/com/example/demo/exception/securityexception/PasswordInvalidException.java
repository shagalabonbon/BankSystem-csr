package com.example.demo.exception.securityexception;

public class PasswordInvalidException extends RuntimeException{

	public PasswordInvalidException() {
		super();
	}

	public PasswordInvalidException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PasswordInvalidException(String message, Throwable cause) {
		super(message, cause);
	}

	public PasswordInvalidException(String message) {
		super(message);
	}

	public PasswordInvalidException(Throwable cause) {
		super(cause);	
	}
	
}
