package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    
    @NotNull(message = "Listing ID is required")
    private Long listingId;
    
    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;
}




