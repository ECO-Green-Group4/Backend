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

    // Fields matching frontend order
    @Column(name = "title", columnDefinition = "nvarchar(255)")
    private String title;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "images", columnDefinition = "nvarchar(max)")
    private String images; // Store as JSON array string or comma-separated URLs

    @Column(name = "location", columnDefinition = "nvarchar(255)")
    private String location;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "brand", columnDefinition = "nvarchar(100)")
    private String brand;

    @Column(name = "model", columnDefinition = "nvarchar(100)")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "body_type", columnDefinition = "nvarchar(50)")
    private String bodyType; // SUV, Sedan, Scooter...

    @Column(name = "color", columnDefinition = "nvarchar(50)")
    private String color;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "inspection", columnDefinition = "nvarchar(255)")
    private String inspection; // Yes/No/Until 2025

    @Column(name = "origin", columnDefinition = "nvarchar(100)")
    private String origin; // Vietnam, China, Japan...

    @Column(name = "number_of_seats")
    private Integer numberOfSeats; // 2/4/5

    @Column(name = "license_plate", columnDefinition = "nvarchar(50)")
    private String licensePlate; // 51F-123.45

    @Column(name = "accessories", columnDefinition = "nvarchar(max)")
    private String accessories; // Helmet, charger, etc.

    @Column(name = "battery_capacity")
    private Double batteryCapacity;

    @Column(name = "condition", columnDefinition = "nvarchar(50)")
    private String condition;

    @Column(name = "status", columnDefinition = "nvarchar(50)")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}