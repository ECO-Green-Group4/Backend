package com.evmarket.trade.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "batteries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "battery_id")
    private Long batteryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Basic battery fields
    @Column(name = "brand")
    private String brand; // Battery Brand (VinFast, CATL, LG...)

    @Column(name = "type")
    private String type; // Battery Type

    @Column(name = "capacity")
    private String capacity; // "32Ah or 2000Wh" - keep as String

    @Column(name = "health_percent")
    private Integer healthPercent; // SoH (%)

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // New fields from Figma form
    @Column(name = "voltage")
    private Double voltage; // Voltage (V) - default 72

    @Column(name = "charge_cycles")
    private Integer chargeCycles; // Charge Cycles - default 300

    @Column(name = "origin")
    private String origin; // Origin (China, Vietnam, Korea...)
}