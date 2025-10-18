package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByUser(User user);
    List<Listing> findByStatus(String status);
    List<Listing> findByItemType(String itemType);
    
    @Query("SELECT l FROM Listing l WHERE l.user = :user AND l.status = :status")
    List<Listing> findByUserAndStatus(@Param("user") User user, @Param("status") String status);
    
    @Query("SELECT l FROM Listing l WHERE l.itemType = :itemType AND l.status = 'ACTIVE'")
    List<Listing> findActiveByItemType(@Param("itemType") String itemType);
    
    @Query("SELECT l FROM Listing l WHERE l.itemType = :itemType AND l.status = :status")
    List<Listing> findByItemTypeAndStatus(@Param("itemType") String itemType, @Param("status") String status);
    
    @Query("SELECT l FROM Listing l WHERE l.title LIKE %:keyword% AND l.status = 'ACTIVE'")
    List<Listing> findByTitleContaining(@Param("keyword") String keyword);
    
    @Query("SELECT l FROM Listing l WHERE l.location LIKE %:location% AND l.status = 'ACTIVE'")
    List<Listing> findByLocationContaining(@Param("location") String location);
}