package com.evmarket.trade.repository;

import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListingPackageRepository extends JpaRepository<ListingPackage, Long> {

    List<ListingPackage> findByListing(Listing listing);

    List<ListingPackage> findByServicePackage(ServicePackage servicePackage);

    List<ListingPackage> findByStatus(String status);

    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing.user = :user")
    List<ListingPackage> findByUser(@Param("user") User user);

    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing = :listing AND lp.status = 'ACTIVE' AND lp.expiredAt > :now")
    Optional<ListingPackage> findActiveByListing(@Param("listing") Listing listing, @Param("now") LocalDateTime now);

    // Note: Membership packages are not directly linked to users in ListingPackage
    // This query is commented out as it doesn't fit the current design
    // @Query("SELECT lp FROM ListingPackage lp WHERE lp.user = :user AND lp.servicePackage.packageType = 'MEMBERSHIP' AND lp.status = 'ACTIVE' AND lp.expiredAt > :now")
    // Optional<ListingPackage> findActiveMembershipByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing = :listing AND lp.servicePackage.packageType = 'LISTING_VIP' AND lp.status = 'ACTIVE' AND lp.expiredAt > :now")
    Optional<ListingPackage> findActiveListingPackage(@Param("listing") Listing listing, @Param("now") LocalDateTime now);

    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing = :listing AND lp.status = :status")
    List<ListingPackage> findByListingAndStatus(@Param("listing") Listing listing, @Param("status") String status);

    @Query("SELECT lp FROM ListingPackage lp WHERE lp.listing.user = :user AND lp.status = 'ACTIVE' AND lp.expiredAt > :now")
    List<ListingPackage> findActiveByUser(@Param("user") User user, @Param("now") LocalDateTime now);
}