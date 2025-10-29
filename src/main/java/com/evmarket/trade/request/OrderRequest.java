package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    
    @NotNull(message = "Listing ID is required")
    private Long listingId;
}




