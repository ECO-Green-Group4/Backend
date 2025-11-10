package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long reviewId;
    private UserInfoResponse reviewer;
    private UserInfoResponse targetUser;
    private Long orderId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

