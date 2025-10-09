package com.evmarket.trade.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final ErrorHandler errorHandler;

    public AppException(ErrorHandler errorHandler) {
        super(errorHandler.getMessage());
        this.errorHandler = errorHandler;
    }
}



