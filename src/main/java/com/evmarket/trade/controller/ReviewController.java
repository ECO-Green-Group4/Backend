package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateReviewRequest;
import com.evmarket.trade.request.UpdateReviewRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AuthService authService;

    // Tạo đánh giá
    @PostMapping
    public ResponseEntity<BaseResponse<?>> createReview(
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication) {
        User reviewer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(reviewService.createReview(request, reviewer));
    }

    // Cập nhật đánh giá
    @PutMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<?>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request,
            Authentication authentication) {
        User reviewer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(reviewService.updateReview(reviewId, request, reviewer));
    }

    // Xem tất cả đánh giá của một user
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<?>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    // Xem đánh giá theo order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<BaseResponse<?>> getReviewsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(reviewService.getReviewsByOrder(orderId));
    }

    // Xem chi tiết một đánh giá
    @GetMapping("/{reviewId}")
    public ResponseEntity<BaseResponse<?>> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    // Xem đánh giá của tôi cho một order cụ thể
    @GetMapping("/order/{orderId}/my-review")
    public ResponseEntity<BaseResponse<?>> getMyReviewForOrder(
            @PathVariable Long orderId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(reviewService.getMyReviewForOrder(orderId, user));
    }

    // Xem điểm trung bình và thống kê đánh giá của một user
    @GetMapping("/user/{userId}/rating")
    public ResponseEntity<BaseResponse<?>> getUserRating(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getUserRating(userId));
    }
}

