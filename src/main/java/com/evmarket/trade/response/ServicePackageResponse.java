package com.evmarket.trade.response;

import java.math.BigDecimal;

public class ServicePackageResponse {
    private Long packageId;
    private String name;
    private Integer listingLimit;
    private BigDecimal listingFee;
    private Boolean highlight;
    private Integer durationDays;
    private BigDecimal commissionDiscount;
    private String status;

    // Constructors
    public ServicePackageResponse() {}

    public ServicePackageResponse(Long packageId, String name, Integer listingLimit,
                                BigDecimal listingFee, Boolean highlight, Integer durationDays,
                                BigDecimal commissionDiscount, String status) {
        this.packageId = packageId;
        this.name = name;
        this.listingLimit = listingLimit;
        this.listingFee = listingFee;
        this.highlight = highlight;
        this.durationDays = durationDays;
        this.commissionDiscount = commissionDiscount;
        this.status = status;
    }

    // Getters and Setters
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
