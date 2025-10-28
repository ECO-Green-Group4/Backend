package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BatteryResponse {
    private Long batteryId;
    private String title;
    private String description;
    private String location;
    private BigDecimal price;
    private String batteryBrand;
    private Double voltage;
    private String capacity; // "32Ah or 2000Wh" - keep as String
    private Integer healthPercent;
    private Integer chargeCycles;
    private String type;
    private Integer manufactureYear;
    private String origin;
    private String status;
    private LocalDateTime createdAt;
    private String sellerName;
    private String sellerPhone;

    // Constructors
    public BatteryResponse() {}

    public BatteryResponse(Long batteryId, String title, String description, String location,
                          BigDecimal price, String batteryBrand, Double voltage, String capacity,
                          Integer healthPercent, Integer chargeCycles, String type,
                          Integer manufactureYear, String origin, String status,
                          LocalDateTime createdAt, String sellerName, String sellerPhone) {
        this.batteryId = batteryId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.batteryBrand = batteryBrand;
        this.voltage = voltage;
        this.capacity = capacity;
        this.healthPercent = healthPercent;
        this.chargeCycles = chargeCycles;
        this.type = type;
        this.manufactureYear = manufactureYear;
        this.origin = origin;
        this.status = status;
        this.createdAt = createdAt;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    // Getters and Setters
    public Long getBatteryId() { return batteryId; }
    public void setBatteryId(Long batteryId) { this.batteryId = batteryId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getBatteryBrand() { return batteryBrand; }
    public void setBatteryBrand(String batteryBrand) { this.batteryBrand = batteryBrand; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public String getCapacity() { return capacity; }
    public void setCapacity(String capacity) { this.capacity = capacity; }
    
    public Integer getHealthPercent() { return healthPercent; }
    public void setHealthPercent(Integer healthPercent) { this.healthPercent = healthPercent; }
    
    public Integer getChargeCycles() { return chargeCycles; }
    public void setChargeCycles(Integer chargeCycles) { this.chargeCycles = chargeCycles; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
}
