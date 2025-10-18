package com.evmarket.trade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Basic vehicle fields
    @Column(name = "brand", columnDefinition = "nvarchar")
    private String brand;

    @Column(name = "model", columnDefinition = "nvarchar")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "battery_capacity")
    private Double batteryCapacity;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "condition", columnDefinition = "nvarchar")
    private String condition;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", columnDefinition = "nvarchar")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // New fields from Figma form
    @Column(name = "body_type", columnDefinition = "nvarchar")
    private String bodyType; // SUV, Sedan, Scooter...

    @Column(name = "color", columnDefinition = "nvarchar")
    private String color; // Red, Blue, White...

    @Column(name = "inspection", columnDefinition = "nvarchar")
    private String inspection; // Yes/No/Until 2025

    @Column(name = "origin", columnDefinition = "nvarchar")
    private String origin; // Vietnam, China, Japan...

    @Column(name = "number_of_seats")
    private Integer numberOfSeats; // 2/4/5

    @Column(name = "license_plate", columnDefinition = "nvarchar")
    private String licensePlate; // 51F-123.45

    @Column(name = "accessories", columnDefinition = "nvarchar")
    private String accessories; // Helmet, charger, etc.
}