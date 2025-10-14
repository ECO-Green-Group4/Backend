package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BatteryResponse {
    private Long batteryId;
    private String type;
    private Double capacity;
    private Integer healthPercent;
    private Integer manufactureYear;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private String sellerName;
    private String sellerPhone;

    // Constructors
    public BatteryResponse() {}

    public BatteryResponse(Long batteryId, String type, Double capacity, Integer healthPercent,
                          Integer manufactureYear, BigDecimal price, String status,
                          LocalDateTime createdAt, String sellerName, String sellerPhone) {
        this.batteryId = batteryId;
        this.type = type;
        this.capacity = capacity;
        this.healthPercent = healthPercent;
        this.manufactureYear = manufactureYear;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    // Getters and Setters
    public Long getBatteryId() { return batteryId; }
    public void setBatteryId(Long batteryId) { this.batteryId = batteryId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    
    public Integer getHealthPercent() { return healthPercent; }
    public void setHealthPercent(Integer healthPercent) { this.healthPercent = healthPercent; }
    
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
}
