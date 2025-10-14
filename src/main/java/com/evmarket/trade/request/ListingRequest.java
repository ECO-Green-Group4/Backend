package com.evmarket.trade.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ListingRequest {
    
    @NotBlank(message = "Item type is required")
    @Pattern(regexp = "^(vehicle|battery)$", message = "Item type must be 'vehicle' or 'battery'")
    private String itemType;
    
    @NotNull(message = "Item ID is required")
    private Long itemId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private String images; // JSON string of image URLs
    
    @NotBlank(message = "Location is required")
    private String location;
}

