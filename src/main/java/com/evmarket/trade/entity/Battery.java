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

    // Fields matching frontend order
    @Column(name = "title", columnDefinition = "nvarchar(255)")
    private String title;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "location", columnDefinition = "nvarchar(255)")
    private String location;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "brand", columnDefinition = "nvarchar(100)")
    private String brand; // Battery Brand (VinFast, CATL, LG...)

    @Column(name = "voltage")
    private Double voltage; // Voltage (V)

    @Column(name = "capacity", columnDefinition = "nvarchar(100)")
    private String capacity; // "32Ah or 2000Wh" - keep as String

    @Column(name = "health_percent")
    private Integer healthPercent; // SoH (%)

    @Column(name = "charge_cycles")
    private Integer chargeCycles; // Charge Cycles

    @Column(name = "type", columnDefinition = "nvarchar(100)")
    private String type; // Battery Type

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "origin", columnDefinition = "nvarchar(100)")
    private String origin; // Origin (China, Vietnam, Korea...)

    @Column(name = "status", columnDefinition = "nvarchar(50)")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}