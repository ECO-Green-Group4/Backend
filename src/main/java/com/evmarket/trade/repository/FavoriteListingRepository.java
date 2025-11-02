package com.evmarket.trade.repository;

import com.evmarket.trade.entity.FavoriteListing;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteListingRepository extends JpaRepository<FavoriteListing, Long> {
    
    Optional<FavoriteListing> findByUserAndListing(User user, Listing listing);
    
    List<FavoriteListing> findByUser(User user);
    
    @Query("SELECT f.listing FROM FavoriteListing f WHERE f.user = :user ORDER BY f.createdAt DESC")
    List<Listing> findListingsByUser(@Param("user") User user);
    
    boolean existsByUserAndListing(User user, Listing listing);
    
    void deleteByUserAndListing(User user, Listing listing);
}


