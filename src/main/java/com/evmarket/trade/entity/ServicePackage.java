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

    @Column(name = "name")
    private String name;

    @Column(name = "listing_limit")
    private Integer listingLimit;

    @Column(name = "listing_fee")
    private BigDecimal listingFee;

    @Column(name = "highlight")
    private Boolean highlight;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "commission_discount")
    private BigDecimal commissionDiscount;

    @Column(name = "status")
    private String status;

    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getListingLimit() { return listingLimit; }
    public void setListingLimit(Integer listingLimit) { this.listingLimit = listingLimit; }
    public BigDecimal getListingFee() { return listingFee; }
    public void setListingFee(BigDecimal listingFee) { this.listingFee = listingFee; }
    public Boolean getHighlight() { return highlight; }
    public void setHighlight(Boolean highlight) { this.highlight = highlight; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public BigDecimal getCommissionDiscount() { return commissionDiscount; }
    public void setCommissionDiscount(BigDecimal commissionDiscount) { this.commissionDiscount = commissionDiscount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}