package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingResponse {
    private Long listingId;
    private UserInfoResponse user;
    private String itemType;
    private String title;
    private String description;
    private List<String> images; // Changed to List<String>
    private String location;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private String postType;
    
    // ListingPackage info for payment
    private Long listingPackageId;
    private BigDecimal packageAmount;
    private String packageStatus;
    private LocalDateTime packageExpiredAt;
    
    // Vehicle fields
    private String brand;
    private String model;
    private Integer year;
    private Double batteryCapacity;
    private Integer mileage;
    private String condition;
    private String bodyType;
    private String color;
    private String inspection;
    private String origin;
    private Integer numberOfSeats;
    private String licensePlate;
    private String accessories;
    
    // Battery fields
    private String batteryBrand;
    private Double voltage;
    private String type;
    private String capacity; // Changed to String
    private Integer healthPercent;
    private Integer manufactureYear;
    private Integer chargeCycles;
    // reuse origin above for battery as well
}
