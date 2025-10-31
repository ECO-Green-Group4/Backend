package com.evmarket.trade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "item_type", columnDefinition = "nvarchar(50)")
    private String itemType; // vehicle or battery

    @Column(name = "item_id")
    private Long itemId; // FK to vehicles/batteries

    @Column(name = "title", columnDefinition = "nvarchar(255)")
    private String title;

    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;

    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_url", columnDefinition = "nvarchar(500)")
    private List<String> images; // Changed to List<String> for multiple images

    @Column(name = "location", columnDefinition = "nvarchar(255)")
    private String location;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", columnDefinition = "nvarchar(50)")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "post_type", columnDefinition = "nvarchar(100)")
    private String postType; // Package name/tier used for posting

    // Vehicle specific fields from Figma form
    @Column(name = "brand", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "model", columnDefinition = "nvarchar(100)")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "body_type", columnDefinition = "nvarchar(50)")
    private String bodyType; // SUV, Sedan, Scooter...

    @Column(name = "color", columnDefinition = "nvarchar(50)")
    private String color; // Red, Blue, White...

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "inspection", columnDefinition = "nvarchar(50)")
    private String inspection; // Yes/No/Until 2025

    @Column(name = "origin", columnDefinition = "nvarchar(50)")
    private String origin; // Vietnam, China, Japan...

    @Column(name = "number_of_seats")
    private Integer numberOfSeats; // 2/4/5

    @Column(name = "license_plate", columnDefinition = "nvarchar(20)")
    private String licensePlate; // 51F-123.45

    @Column(name = "accessories", columnDefinition = "nvarchar(500)")
    private String accessories; // Helmet, charger, etc.

    @Column(name = "battery_capacity")
    private Double batteryCapacity;

    @Column(name = "condition", columnDefinition = "nvarchar(50)")
    private String condition;

    // Battery specific fields from Figma form
    @Column(name = "battery_brand", columnDefinition = "nvarchar(100)")
    private String batteryBrand; // VinFast, CATL, LG...

    @Column(name = "voltage")
    private Double voltage; // Voltage (V)

    @Column(name = "capacity", columnDefinition = "nvarchar(100)")
    private String capacity; // "32Ah or 2000Wh" - keep as String

    @Column(name = "health_percent")
    private Integer healthPercent; // SoH (%)

    @Column(name = "charge_cycles")
    private Integer chargeCycles; // Charge Cycles

    @Column(name = "type", columnDefinition = "nvarchar(50)")
    private String type; // Battery Type

    @Column(name = "manufacture_year")
    private Integer manufactureYear;
    
    @Column(name = "package_quantity")
    private Integer packageQuantity = 1; // Quantity of packages purchased
}