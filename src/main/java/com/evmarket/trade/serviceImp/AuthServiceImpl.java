package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.exception.ErrorHandler;
import com.evmarket.trade.repository.UserRepository;
import com.evmarket.trade.response.LoginResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.request.LoginRequest;
import com.evmarket.trade.request.RegisterRequest;
import com.evmarket.trade.security.JwtService;
import com.evmarket.trade.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public ResponseEntity<String> register(RegisterRequest request) {
        validateRegister(request);
        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhoneNumber().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("member");
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getSex().trim());
        user.setIdentityCard(request.getIdentityCard().trim());
        user.setAddress(request.getAddress().trim());
        user = userRepository.save(user);

        return ResponseEntity.ok("Registration successful");
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        if (!StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPassword())) {
            throw new AppException(ErrorHandler.INVALID_INPUT);
        }
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new AppException(ErrorHandler.CREDENTIALS_INVALID));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorHandler.CREDENTIALS_INVALID);
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        LoginResponse response = LoginResponse.builder()
                .message("Login successful")
                .role(user.getRole())
                .token(token)
                .id(user.getUserId())
                .sex(user.getGender())
                .fullName(user.getFullName())
                .build();
        return ResponseEntity.ok(response);
    }

    private void validateRegister(RegisterRequest request) {
        if (!StringUtils.hasText(request.getFullName()) || !StringUtils.hasText(request.getUsername()) || 
            !StringUtils.hasText(request.getEmail()) || !StringUtils.hasText(request.getPhoneNumber()) || 
            !StringUtils.hasText(request.getPassword()) || request.getDateOfBirth() == null || 
            !StringUtils.hasText(request.getSex()) || !StringUtils.hasText(request.getIdentityCard()) || 
            !StringUtils.hasText(request.getAddress())) {
            throw new AppException(ErrorHandler.INVALID_INPUT);
        }
        if (userRepository.existsByUsername(request.getUsername().trim())) {
            throw new AppException(ErrorHandler.USERNAME_EXIST);
        }
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new AppException(ErrorHandler.EMAIL_EXIST);
        }
        if (userRepository.existsByIdentityCard(request.getIdentityCard().trim())) {
            throw new AppException(ErrorHandler.IDENTITY_CARD_EXIST);
        }
        // email simple pattern check
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new AppException(ErrorHandler.EMAIL_INVALID);
        }
        // phone VN common pattern: +84xxxxxxxxx or 0xxxxxxxxx (9-11 digits)
        if (!request.getPhoneNumber().matches("^(\\+?84|0)\\d{9,10}$")) {
            throw new AppException(ErrorHandler.PHONE_INVALID);
        }
        // identity card digits 9-20
        if (!request.getIdentityCard().matches("^\\d{9,20}$")) {
            throw new AppException(ErrorHandler.IDENTITY_CARD_INVALID);
        }
        // password min 8 with at least 1 letter and 1 digit
        if (!request.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
            throw new AppException(ErrorHandler.PASSWORD_WEAK);
        }
        // date of birth not in future
        if (request.getDateOfBirth().isAfter(java.time.LocalDate.now())) {
            throw new AppException(ErrorHandler.DATE_OF_BIRTH_INVALID);
        }
    }
    
    @Override
    public User getCurrentUser(Authentication authentication) {
        String subject = authentication.getName();
        // subject is email now
        return userRepository.findByEmail(subject)
                .orElseThrow(() -> new AppException(ErrorHandler.USER_NOT_EXISTED));
    }
    
    @Override
    public UserInfoResponse getUserProfile(Authentication authentication) {
        User user = getCurrentUser(authentication);
        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }
}


