package com.evmarket.trade.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BatteryRequest {
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Capacity is required")
    private String capacity; // "32Ah or 2000Wh" - keep as String
    
    @NotNull(message = "Health percent is required")
    @Min(value = 0, message = "Health percent must be between 0-100")
    @Max(value = 100, message = "Health percent must be between 0-100")
    private Integer healthPercent;
    
    @NotNull(message = "Manufacture year is required")
    @Min(value = 1900, message = "Manufacture year must be valid")
    @Max(value = 2030, message = "Manufacture year must be valid")
    private Integer manufactureYear;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
}

