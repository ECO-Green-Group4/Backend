package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBatteryListingRequest {
    
    // General listing fields
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private List<String> images; // Changed to List<String> for multiple images
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    // Battery specific fields from Figma form
    @NotBlank(message = "Battery brand is required")
    private String brand; // Battery Brand (VinFast, CATL, LG...)
    
    @NotNull(message = "Voltage is required")
    @Positive(message = "Voltage must be positive")
    private Double voltage; // Voltage (V) - default 72
    
    @NotBlank(message = "Capacity is required")
    private String capacity; // Capacity (Ah / Wh) - "32Ah or 2000Wh"
    
    @NotNull(message = "State of Health (SoH) is required")
    @Positive(message = "State of Health must be positive")
    private Integer healthPercent; // SoH (%) - default 85
    
    @NotNull(message = "Charge cycles is required")
    @Positive(message = "Charge cycles must be positive")
    private Integer chargeCycles; // Charge Cycles - default 300
    
    @NotBlank(message = "Battery type is required")
    private String type; // Battery Type (Select Type)
    
    @NotBlank(message = "Origin is required")
    private String origin; // Origin (China, Vietnam, Korea...)
    
    // Additional battery fields
    @NotNull(message = "Manufacture year is required")
    @Positive(message = "Manufacture year must be positive")
    private Integer manufactureYear;
    
    // Post type selection
    private String postType; // Post Type (For Sale, Wanted, Lease...)
}
