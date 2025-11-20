package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.exception.ErrorHandler;
import com.evmarket.trade.repository.UserRepository;
import com.evmarket.trade.response.LoginResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.request.ForgotPasswordRequest;
import com.evmarket.trade.request.LoginRequest;
import com.evmarket.trade.request.RegisterRequest;
import com.evmarket.trade.request.ResetPasswordRequest;
import com.evmarket.trade.request.ChangePasswordRequest;
import com.evmarket.trade.request.GoogleLoginRequest;
import com.evmarket.trade.request.UpdateProfileRequest;
import com.evmarket.trade.security.JwtService;
import com.evmarket.trade.security.GoogleTokenVerifier;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.EmailService;
import com.evmarket.trade.util.OTPUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Store OTP with email and expiration time (email -> {otp, expirationTime})
    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    
    private static class OtpData {
        String otp;
        LocalDateTime expirationTime;
        
        OtpData(String otp, LocalDateTime expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }
    }

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
        // password min 8 with uppercase, lowercase, digit and special character
        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
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
        
        // Debug log để kiểm tra membership fields
        log.info("User membership info - userId: {}, currentMembershipId: {}, membershipExpiry: {}, availableCoupons: {}", 
                user.getUserId(), user.getCurrentMembershipId(), user.getMembershipExpiry(), user.getAvailableCoupons());
        
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
                .currentMembershipId(user.getCurrentMembershipId())
                .membershipExpiry(user.getMembershipExpiry())
                .availableCoupons(user.getAvailableCoupons())
                .build();
    }
    
    @Override
    public ResponseEntity<String> forgotPassword(ForgotPasswordRequest request) {
        try {
            String email = request.getEmail().trim().toLowerCase();
            
            // Check if user exists - SILENTLY FAIL for security (prevent user enumeration)
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isPresent()) {
                // Only generate and send OTP if user exists
                String otp = OTPUtil.generateOTP();
                
                // Store OTP with 5 minutes expiration
                LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
                otpStorage.put(email, new OtpData(otp, expirationTime));
                
                // Send OTP via email
                emailService.sendOtpEmail(email, otp);
            }
            
            // ALWAYS return the same message regardless of whether user exists or not
            // This prevents attackers from enumerating valid email addresses
            return ResponseEntity.ok("If your email is registered, an OTP has been sent. The code is valid for 5 minutes.");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to process request: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        try {
            String email = request.getEmail().trim().toLowerCase();
            String otp = request.getOtp().trim();
            String newPassword = request.getNewPassword();
            
            // Check if user exists
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorHandler.USER_NOT_EXISTED));
            
            // Get OTP data from storage
            OtpData otpData = otpStorage.get(email);
            
            // Verify OTP
            if (otpData == null) {
                return ResponseEntity.badRequest().body("OTP does not exist or has expired");
            }
            
            // Check if OTP is expired
            if (LocalDateTime.now().isAfter(otpData.expirationTime)) {
                otpStorage.remove(email);
                return ResponseEntity.badRequest().body("OTP has expired");
            }
            
            // Verify OTP match
            if (!otpData.otp.equals(otp)) {
                return ResponseEntity.badRequest().body("Invalid OTP");
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            // Remove OTP from storage after successful reset
            otpStorage.remove(email);
            
            return ResponseEntity.ok("Password has been reset successfully");
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Unable to reset password: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<String> changePassword(ChangePasswordRequest request, Authentication authentication) {
        try {
            // Get current user from authentication
            User currentUser = getCurrentUser(authentication);
            
            String currentPassword = request.getCurrentPassword();
            String newPassword = request.getNewPassword();
            String confirmPassword = request.getConfirmPassword();
            
            // Validate new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body("New password and confirm password do not match");
            }
            
            // Validate current password
            if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
                return ResponseEntity.badRequest().body("Current password is incorrect");
            }
            
            // Check if new password is different from current password
            if (passwordEncoder.matches(newPassword, currentUser.getPassword())) {
                return ResponseEntity.badRequest().body("New password must be different from current password");
            }
            
            // Update password
            currentUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(currentUser);
            
            log.info("Password changed successfully for user: {}", currentUser.getEmail());
            
            return ResponseEntity.ok("Password has been changed successfully");
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing password: ", e);
            return ResponseEntity.badRequest().body("Unable to change password: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<LoginResponse> loginWithGoogle(GoogleLoginRequest request) {
        // Validate request - NEW: require idToken
        if (!StringUtils.hasText(request.getIdToken())) {
            throw new AppException(ErrorHandler.INVALID_INPUT);
        }
        
        // SECURITY FIX: Verify Google ID Token with Google
        Optional<GoogleTokenVerifier.GoogleUserInfo> googleUserInfo = 
                googleTokenVerifier.verifyToken(request.getIdToken());
        
        if (googleUserInfo.isEmpty()) {
            log.warn("Invalid or expired Google ID token");
            throw new AppException(ErrorHandler.CREDENTIALS_INVALID);
        }
        
        // Extract verified user info from Google
        String email = googleUserInfo.get().getEmail().toLowerCase();
        String providerId = googleUserInfo.get().getProviderId();
        String fullName = googleUserInfo.get().getFullName();
        String profilePicture = googleUserInfo.get().getProfilePicture();
        
        log.info("Google token verified successfully for user: {}", email);
        
        // Check if user exists by email
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        User user;
        if (existingUser.isPresent()) {
            // User exists - update provider info if not set
            user = existingUser.get();
            if (user.getProvider() == null || user.getProviderId() == null) {
                user.setProvider("GOOGLE");
                user.setProviderId(providerId);
                user = userRepository.save(user);
            } else if (!"GOOGLE".equals(user.getProvider()) || !providerId.equals(user.getProviderId())) {
                // Provider mismatch - user trying to login with different provider
                log.warn("Provider mismatch for user: {} (expected: {}, got: {})", 
                        email, user.getProvider(), providerId);
                throw new AppException(ErrorHandler.CREDENTIALS_INVALID);
            }
        } else {
            // New user - create account with Google
            user = new User();
            user.setEmail(email);
            user.setFullName(fullName != null ? fullName.trim() : email.split("@")[0]);
            // Generate unique username from email
            String baseUsername = email.split("@")[0];
            String username = baseUsername;
            int counter = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter;
                counter++;
            }
            user.setUsername(username);
            // Generate random password (never used but required for database)
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            user.setRole("member");
            user.setProvider("GOOGLE");
            user.setProviderId(providerId);
            user.setStatus("active");
            user.setCreatedAt(LocalDateTime.now());
            
            // Set default values for optional fields to avoid NULL issues
            user.setPhone("");
            user.setAddress("");
            user.setGender("other");
            user.setIdentityCard("");
            
            user = userRepository.save(user);
            
            log.info("New Google user registered: {}", email);
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        
        // Check if profile is complete
        boolean profileComplete = isProfileComplete(user);
        
        LoginResponse response = LoginResponse.builder()
                .message("Login with Google successful")
                .role(user.getRole())
                .token(token)
                .id(user.getUserId())
                .sex(user.getGender())
                .fullName(user.getFullName())
                .profileComplete(profileComplete)
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if user profile is complete
     * Profile is complete when all required fields are filled
     */
    private boolean isProfileComplete(User user) {
        // Check required fields
        boolean hasPhone = user.getPhone() != null && !user.getPhone().trim().isEmpty();
        boolean hasAddress = user.getAddress() != null && !user.getAddress().trim().isEmpty();
        boolean hasIdentityCard = user.getIdentityCard() != null && !user.getIdentityCard().trim().isEmpty();
        boolean hasDateOfBirth = user.getDateOfBirth() != null;
        boolean hasGender = user.getGender() != null && !user.getGender().trim().isEmpty() && !user.getGender().equals("other");
        
        // Profile is complete if ALL fields are filled
        return hasPhone && hasAddress && hasIdentityCard && hasDateOfBirth && hasGender;
    }
    
    @Override
    @Transactional
    public ResponseEntity<String> updateProfile(UpdateProfileRequest request, Authentication authentication) {
        try {
            // Get current user
            User user = getCurrentUser(authentication);
            
            // Update user profile fields
            user.setPhone(request.getPhone().trim());
            user.setAddress(request.getAddress().trim());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setGender(request.getGender().trim());
            user.setIdentityCard(request.getIdentityCard().trim());
            
            // Save updated user
            userRepository.save(user);
            
            log.info("Profile updated successfully for user: {}", user.getEmail());
            
            return ResponseEntity.ok("Profile updated successfully");
            
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating profile: ", e);
            return ResponseEntity.badRequest().body("Unable to update profile: " + e.getMessage());
        }
    }
}


