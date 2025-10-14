package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateOrderRequest {
    
    @NotNull(message = "Listing ID is required")
    @Positive(message = "Listing ID must be positive")
    private Long listingId;

    // Getters and Setters
    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
}
