package com.evmarket.trade.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicepackage")
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Long packageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type", nullable = false)
    private PackageType packageType; // LISTING_VIP, MEMBERSHIP

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "listing_limit")
    private Integer listingLimit;

    // GIỮ NGUYÊN listing_fee thay vì đổi thành price
    @Column(name = "listing_fee", precision = 15, scale = 2)
    private BigDecimal listingFee;

    @Column(name = "highlight")
    private Boolean highlight;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "commission_discount", precision = 5, scale = 2)
    private BigDecimal commissionDiscount;

    @Column(name = "coupon_count")
    private Integer couponCount;

    @Column(name = "status", length = 20)
    private String status;

    public enum PackageType {
        LISTING_VIP,    // Gói tin VIP
        MEMBERSHIP      // Gói membership
    }

    // Constructors
    public ServicePackage() {}

    // Getters and Setters - GIỮ NGUYÊN listingFee
    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }

    public PackageType getPackageType() { return packageType; }
    public void setPackageType(PackageType packageType) { this.packageType = packageType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getListingLimit() { return listingLimit; }
    public void setListingLimit(Integer listingLimit) { this.listingLimit = listingLimit; }

    public BigDecimal getListingFee() { return listingFee; } // GIỮ NGUYÊN
    public void setListingFee(BigDecimal listingFee) { this.listingFee = listingFee; } // GIỮ NGUYÊN

    public Boolean getHighlight() { return highlight; }
    public void setHighlight(Boolean highlight) { this.highlight = highlight; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public BigDecimal getCommissionDiscount() { return commissionDiscount; }
    public void setCommissionDiscount(BigDecimal commissionDiscount) { this.commissionDiscount = commissionDiscount; }

    public Integer getCouponCount() { return couponCount; }
    public void setCouponCount(Integer couponCount) { this.couponCount = couponCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}