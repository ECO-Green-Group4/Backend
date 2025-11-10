package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateReviewRequest;
import com.evmarket.trade.request.UpdateReviewRequest;
import com.evmarket.trade.response.ReviewResponse;
import com.evmarket.trade.response.UserRatingResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface ReviewService {
    BaseResponse<ReviewResponse> createReview(CreateReviewRequest request, User reviewer);
    BaseResponse<ReviewResponse> updateReview(Long reviewId, UpdateReviewRequest request, User reviewer);
    BaseResponse<List<ReviewResponse>> getReviewsByUser(Long userId);
    BaseResponse<List<ReviewResponse>> getReviewsByOrder(Long orderId);
    BaseResponse<ReviewResponse> getReviewById(Long reviewId);
    BaseResponse<UserRatingResponse> getUserRating(Long userId);
    BaseResponse<ReviewResponse> getMyReviewForOrder(Long orderId, User user);
}

