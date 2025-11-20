package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Review;
import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.ReviewRepository;
import com.evmarket.trade.repository.OrderRepository;
import com.evmarket.trade.repository.UserRepository;
import com.evmarket.trade.request.CreateReviewRequest;
import com.evmarket.trade.request.UpdateReviewRequest;
import com.evmarket.trade.response.ReviewResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.UserRatingResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public BaseResponse<ReviewResponse> createReview(CreateReviewRequest request, User reviewer) {
        try {
            // Validate order exists
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));

            // Validate target user exists
            User targetUser = userRepository.findById(request.getTargetUserId())
                    .orElseThrow(() -> new AppException("Không tìm thấy người dùng với ID: " + request.getTargetUserId()));

            // Validate reviewer is buyer or seller of the order
            boolean isBuyer = order.getBuyer().getUserId() == reviewer.getUserId();
            boolean isSeller = order.getSeller().getUserId() == reviewer.getUserId();
            
            if (!isBuyer && !isSeller) {
                throw new AppException("Bạn không có quyền đánh giá đơn hàng này");
            }

            // Validate target user is the other party (buyer reviews seller, seller reviews buyer)
            boolean targetIsBuyer = order.getBuyer().getUserId() == targetUser.getUserId();
            boolean targetIsSeller = order.getSeller().getUserId() == targetUser.getUserId();
            
            if (!targetIsBuyer && !targetIsSeller) {
                throw new AppException("Người được đánh giá phải là người mua hoặc người bán của đơn hàng");
            }

            // Cannot review yourself
            if (reviewer.getUserId() == targetUser.getUserId()) {
                throw new AppException("Bạn không thể đánh giá chính mình");
            }

            // Check if order is completed (only allow review after order completion)
            if (!"COMPLETED".equals(order.getStatus()) && !"DELIVERED".equals(order.getStatus())) {
                throw new AppException("Chỉ có thể đánh giá sau khi đơn hàng đã hoàn thành");
            }

            // Check if review already exists for this order and reviewer
            Optional<Review> existingReview = reviewRepository.findByReviewerAndTargetUserAndOrder(
                    reviewer, targetUser, order);
            if (existingReview.isPresent()) {
                throw new AppException("Bạn đã đánh giá người dùng này cho đơn hàng này rồi");
            }

            // Create review
            Review review = new Review();
            review.setReviewer(reviewer);
            review.setTargetUser(targetUser);
            review.setOrder(order);
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setCreatedAt(LocalDateTime.now());

            Review savedReview = reviewRepository.save(review);
            return BaseResponse.success(convertToResponse(savedReview), "Đánh giá đã được tạo thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi tạo đánh giá: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ReviewResponse> updateReview(Long reviewId, UpdateReviewRequest request, User reviewer) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new AppException("Không tìm thấy đánh giá với ID: " + reviewId));

            // Check if reviewer is the owner of the review
            if (review.getReviewer().getUserId() != reviewer.getUserId()) {
                throw new AppException("Bạn không có quyền chỉnh sửa đánh giá này");
            }

            // Check if review is too old (optional: only allow update within 7 days)
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            if (review.getCreatedAt().isBefore(sevenDaysAgo)) {
                throw new AppException("Không thể chỉnh sửa đánh giá sau 7 ngày");
            }

            // Update review
            if (request.getRating() != null) {
                review.setRating(request.getRating());
            }
            if (request.getComment() != null) {
                review.setComment(request.getComment());
            }

            Review updatedReview = reviewRepository.save(review);
            return BaseResponse.success(convertToResponse(updatedReview), "Đánh giá đã được cập nhật thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi cập nhật đánh giá: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ReviewResponse>> getReviewsByUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException("Không tìm thấy người dùng với ID: " + userId));

            List<Review> reviews = reviewRepository.findByTargetUser(user);
            List<ReviewResponse> responses = reviews.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Danh sách đánh giá đã được lấy thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ReviewResponse>> getReviewsByOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy đơn hàng với ID: " + orderId));

            List<Review> reviews = reviewRepository.findByOrder(order);
            List<ReviewResponse> responses = reviews.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Danh sách đánh giá của đơn hàng đã được lấy thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách đánh giá: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<ReviewResponse> getReviewById(Long reviewId) {
        try {
            Review review = reviewRepository.findById(reviewId)
                    .orElseThrow(() -> new AppException("Không tìm thấy đánh giá với ID: " + reviewId));

            return BaseResponse.success(convertToResponse(review), "Đánh giá đã được lấy thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy đánh giá: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<UserRatingResponse> getUserRating(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException("Không tìm thấy người dùng với ID: " + userId));

            Double averageRating = reviewRepository.getAverageRatingByTargetUser(user);
            Long totalReviews = reviewRepository.countByTargetUser(user);

            // Count reviews by rating
            List<Review> allReviews = reviewRepository.findByTargetUser(user);
            long fiveStar = allReviews.stream().filter(r -> r.getRating() == 5).count();
            long fourStar = allReviews.stream().filter(r -> r.getRating() == 4).count();
            long threeStar = allReviews.stream().filter(r -> r.getRating() == 3).count();
            long twoStar = allReviews.stream().filter(r -> r.getRating() == 2).count();
            long oneStar = allReviews.stream().filter(r -> r.getRating() == 1).count();

            UserRatingResponse response = UserRatingResponse.builder()
                    .userId((long) user.getUserId())
                    .fullName(user.getFullName())
                    .averageRating(averageRating != null ? averageRating : 0.0)
                    .totalReviews(totalReviews)
                    .fiveStarReviews(fiveStar)
                    .fourStarReviews(fourStar)
                    .threeStarReviews(threeStar)
                    .twoStarReviews(twoStar)
                    .oneStarReviews(oneStar)
                    .build();

            return BaseResponse.success(response, "Thông tin đánh giá đã được lấy thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy thông tin đánh giá: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<ReviewResponse> getMyReviewForOrder(Long orderId, User user) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy đơn hàng với ID: " + orderId));

            // Check if user is buyer or seller
            boolean isBuyer = order.getBuyer().getUserId() == user.getUserId();
            boolean isSeller = order.getSeller().getUserId() == user.getUserId();
            
            if (!isBuyer && !isSeller) {
                throw new AppException("Bạn không có quyền xem đánh giá của đơn hàng này");
            }

            // Find review by this user for the other party
            User targetUser = isBuyer ? order.getSeller() : order.getBuyer();
            Optional<Review> review = reviewRepository.findByReviewerAndTargetUserAndOrder(user, targetUser, order);

            if (review.isPresent()) {
                return BaseResponse.success(convertToResponse(review.get()), "Đánh giá đã được lấy thành công");
            } else {
                return BaseResponse.success(null, "Bạn chưa đánh giá cho đơn hàng này");
            }

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy đánh giá: " + e.getMessage());
        }
    }

    private ReviewResponse convertToResponse(Review review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .reviewer(convertUserToInfo(review.getReviewer()))
                .targetUser(convertUserToInfo(review.getTargetUser()))
                .orderId(review.getOrder() != null ? review.getOrder().getOrderId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private UserInfoResponse convertUserToInfo(User user) {
        if (user == null) return null;
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

