package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SelectPackageRequest {
    
    @NotNull(message = "Listing ID is required")
    @Positive(message = "Listing ID must be positive")
    private Long listingId;
    
    @NotNull(message = "Package ID is required")
    @Positive(message = "Package ID must be positive")
    private Long packageId;

    // Getters and Setters
    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
    
    public Long getPackageId() { return packageId; }
    public void setPackageId(Long packageId) { this.packageId = packageId; }
}