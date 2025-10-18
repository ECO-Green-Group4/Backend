package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleResponse {
    private Long vehicleId;
    private String brand;
    private String model;
    private Integer year;
    private Double batteryCapacity;
    private Integer mileage;
    private String condition;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private String sellerName;
    private String sellerPhone;

    // Constructors
    public VehicleResponse() {}

    public VehicleResponse(Long vehicleId, String brand, String model, Integer year, 
                          Double batteryCapacity, Integer mileage, String condition, 
                          BigDecimal price, String status, LocalDateTime createdAt,
                          String sellerName, String sellerPhone) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.batteryCapacity = batteryCapacity;
        this.mileage = mileage;
        this.condition = condition;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Double getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Double batteryCapacity) { this.batteryCapacity = batteryCapacity; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
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
