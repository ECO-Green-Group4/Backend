package com.evmarket.trade.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleRequest {
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    @Max(value = 2030, message = "Year must be valid")
    private Integer year;
    
    @NotNull(message = "Battery capacity is required")
    @DecimalMin(value = "0.0", message = "Battery capacity must be positive")
    private Double batteryCapacity;
    
    @NotNull(message = "Mileage is required")
    @Min(value = 0, message = "Mileage must be non-negative")
    private Integer mileage;
    
    @NotBlank(message = "Condition is required")
    private String condition;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
}

