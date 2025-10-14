package com.evmarket.trade.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private int userId;
    private String fullName;
    private String email;
    private String username;
    private String phone;
    private String role;
    private String status;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private LocalDateTime createdAt;
    
    // Constructor để convert từ User entity
    public UserResponse(com.evmarket.trade.entity.User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.address = user.getAddress();
        this.createdAt = user.getCreatedAt();
        // Không include password và identityCard
    }
}



