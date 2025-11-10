package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Review;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Tìm tất cả đánh giá của một user (người được đánh giá)
    List<Review> findByTargetUser(User targetUser);
    
    // Tìm tất cả đánh giá do một user tạo (người đánh giá)
    List<Review> findByReviewer(User reviewer);
    
    // Tìm đánh giá theo order
    List<Review> findByOrder(Order order);
    
    // Tìm đánh giá của reviewer cho một order cụ thể
    Optional<Review> findByReviewerAndOrder(User reviewer, Order order);
    
    // Tìm đánh giá của reviewer cho targetUser trong một order
    Optional<Review> findByReviewerAndTargetUserAndOrder(User reviewer, User targetUser, Order order);
    
    // Tính điểm trung bình của một user
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser = :targetUser")
    Double getAverageRatingByTargetUser(@Param("targetUser") User targetUser);
    
    // Đếm số lượng đánh giá của một user
    @Query("SELECT COUNT(r) FROM Review r WHERE r.targetUser = :targetUser")
    Long countByTargetUser(@Param("targetUser") User targetUser);
    
    // Lấy tất cả đánh giá của một order (có thể có đánh giá từ buyer và seller)
    @Query("SELECT r FROM Review r WHERE r.order = :order")
    List<Review> findAllByOrder(@Param("order") Order order);
}

