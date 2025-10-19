package com.evmarket.trade.controller;

import com.evmarket.trade.response.LoginResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.request.LoginRequest;
import com.evmarket.trade.request.RegisterRequest;
import com.evmarket.trade.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "User login", description = "Authenticate by email and return JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Login Request received: " + request);
        return authService.login(request);
    }

    @Operation(summary = "User registration", description = "Register a new customer account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("Register Request received: " + request);
        return authService.register(request);
    }

    @Operation(summary = "Get user profile", description = "Get current logged in user's profile information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getUserProfile(authentication));
    }
}


