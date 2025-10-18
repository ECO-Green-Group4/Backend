package com.evmarket.trade.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String username;
    private String phone;
    private String status;
    private String dateOfBirth;
    private String gender;
    private String identityCard;
    private String address;
    private LocalDateTime createdAt;
}