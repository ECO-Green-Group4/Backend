package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
    List<Order> findBySeller(User seller);
    List<Order> findByListing(Listing listing);
    List<Order> findByStatus(String status);
    
    @Query("SELECT o FROM Order o WHERE o.buyer = :buyer AND o.status = :status")
    List<Order> findByBuyerAndStatus(@Param("buyer") User buyer, @Param("status") String status);
    
    @Query("SELECT o FROM Order o WHERE o.seller = :seller AND o.status = :status")
    List<Order> findBySellerAndStatus(@Param("seller") User seller, @Param("status") String status);
    
    @Query("SELECT o FROM Order o WHERE o.listing = :listing AND o.status = 'PENDING'")
    List<Order> findPendingByListing(@Param("listing") Listing listing);
}
