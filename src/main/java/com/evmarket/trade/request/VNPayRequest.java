package com.evmarket.trade.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayRequest {
    
    // Payment ID in our system
    private Long paymentId;
    
    // Payment amount
    private BigDecimal amount;
    
    // Order information
    private String orderInfo;
    
    // Transaction reference code at merchant system (unique)
    private String txnRef;
    
    // Customer's IP address
    private String ipAddress;
    
    // Bank code (optional, leave empty = display all banks)
    private String bankCode;
    
    // Interface language (vn or en)
    private String locale;
}

