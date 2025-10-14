package com.evmarket.trade.repository;

import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingPackageRepository extends JpaRepository<ListingPackage, Long> {
    List<ListingPackage> findByListing(Listing listing);
    
    List<ListingPackage> findByServicePackage(ServicePackage servicePackage);
    
    List<ListingPackage> findByStatus(String status);
    
    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing = :listing AND lp.status = 'ACTIVE'")
    Optional<ListingPackage> findActiveByListing(@Param("listing") Listing listing);
    
    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing = :listing AND lp.status = :status")
    List<ListingPackage> findByListingAndStatus(@Param("listing") Listing listing, @Param("status") String status);
}