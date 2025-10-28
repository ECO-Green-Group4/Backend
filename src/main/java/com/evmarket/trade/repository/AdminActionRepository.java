package com.evmarket.trade.repository;

import com.evmarket.trade.entity.AdminAction;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdminActionRepository extends JpaRepository<AdminAction, Long> {
    
    /**
     * Tìm admin actions theo admin
     */
    List<AdminAction> findByAdminOrderByCreatedAtDesc(User admin);
    
    /**
     * Tìm admin actions theo target type
     */
    List<AdminAction> findByTargetTypeOrderByCreatedAtDesc(String targetType);
    
    /**
     * Tìm admin actions theo target ID
     */
    List<AdminAction> findByTargetIdOrderByCreatedAtDesc(Long targetId);
    
    /**
     * Tìm admin actions theo action type
     */
    List<AdminAction> findByActionOrderByCreatedAtDesc(String action);
    
    /**
     * Tìm admin actions trong khoảng thời gian
     */
    @Query("SELECT aa FROM AdminAction aa WHERE aa.createdAt BETWEEN :startDate AND :endDate ORDER BY aa.createdAt DESC")
    List<AdminAction> findByCreatedAtBetweenOrderByCreatedAtDesc(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Đếm số admin actions theo admin
     */
    Long countByAdmin(User admin);
    
    /**
     * Đếm số admin actions theo action type
     */
    Long countByAction(String action);
}

