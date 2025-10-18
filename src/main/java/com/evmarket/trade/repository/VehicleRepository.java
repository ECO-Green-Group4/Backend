package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Vehicle;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findBySeller(User seller);
    List<Vehicle> findByStatus(String status);
    
    @Query("SELECT v FROM Vehicle v WHERE v.seller = :seller AND v.status = :status")
    List<Vehicle> findBySellerAndStatus(@Param("seller") User seller, @Param("status") String status);
}