package com.springboot.gotgam.core.exception;

import lombok.Getter;

/**
 * 애플리케이션 예외의 기본 클래스
 */
@Getter
public abstract class BaseException extends RuntimeException {
    
    private final String errorCode;
    
    protected BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
