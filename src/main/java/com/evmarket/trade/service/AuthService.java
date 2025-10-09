package com.evmarket.trade.service;

import com.evmarket.trade.response.LoginResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.request.LoginRequest;
import com.evmarket.trade.request.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<BaseResponse<Void>> register(RegisterRequest request);
    ResponseEntity<BaseResponse<LoginResponse>> login(LoginRequest request);
}


