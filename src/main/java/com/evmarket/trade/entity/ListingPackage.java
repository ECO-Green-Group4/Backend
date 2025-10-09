package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "listingpackage")
public class ListingPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_package_id")
    private Long listingPackageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private ServicePackage servicePackage;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "status")
    private String status;

    public Long getListingPackageId() { return listingPackageId; }
    public void setListingPackageId(Long listingPackageId) { this.listingPackageId = listingPackageId; }
    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }
    public ServicePackage getServicePackage() { return servicePackage; }
    public void setServicePackage(ServicePackage servicePackage) { this.servicePackage = servicePackage; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public void setExpiredAt(LocalDateTime expiredAt) { this.expiredAt = expiredAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}