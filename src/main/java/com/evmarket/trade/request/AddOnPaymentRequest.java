package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddOnPaymentRequest {
    
    @NotNull(message = "Contract AddOn ID is required")
    private Long contractAddOnId;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}




