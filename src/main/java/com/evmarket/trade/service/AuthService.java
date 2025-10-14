package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.LoginResponse;
import com.evmarket.trade.request.LoginRequest;
import com.evmarket.trade.request.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    ResponseEntity<String> register(RegisterRequest request);
    ResponseEntity<LoginResponse> login(LoginRequest request);
    User getCurrentUser(Authentication authentication);
}


