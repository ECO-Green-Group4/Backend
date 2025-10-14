package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Battery;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long> {
    List<Battery> findBySeller(User seller);
    List<Battery> findByStatus(String status);
    
    @Query("SELECT b FROM Battery b WHERE b.seller = :seller AND b.status = :status")
    List<Battery> findBySellerAndStatus(@Param("seller") User seller, @Param("status") String status);
}