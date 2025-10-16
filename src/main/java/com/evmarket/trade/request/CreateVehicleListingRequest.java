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
public class CreateVehicleListingRequest {
    
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
    
    // Vehicle specific fields from Figma form
    @NotBlank(message = "Car brand is required")
    private String brand; // Car Brand
    
    @NotBlank(message = "Model trim is required")
    private String model; // Model Trim
    
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year; // Year
    
    @NotBlank(message = "Body type is required")
    private String bodyType; // Body Type (SUV, Sedan, Scooter...)
    
    @NotBlank(message = "Color is required")
    private String color; // Color
    
    @NotNull(message = "Mileage is required")
    @Positive(message = "Mileage must be positive")
    private Integer mileage; // Mileage (km)
    
    @NotBlank(message = "Inspection is required")
    private String inspection; // Inspection (Yes/No/Until 2025)
    
    @NotBlank(message = "Origin is required")
    private String origin; // Origin (Vietnam, China, Japan...)
    
    @NotNull(message = "Number of seats is required")
    @Positive(message = "Number of seats must be positive")
    private Integer numberOfSeats; // Number of Seats (2/4/5)
    
    private String licensePlate; // License Plate (optional)
    
    private String accessories; // Accessories (Helmet, charger, etc.)
    
    // Additional vehicle fields
    @NotNull(message = "Battery capacity is required")
    @Positive(message = "Battery capacity must be positive")
    private Double batteryCapacity;
    
    @NotBlank(message = "Condition is required")
    private String condition;
    
    // Post type selection
    private String postType; // Post Type (For Sale, Wanted, Lease...)
}
