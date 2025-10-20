package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleResponse {
    private Long vehicleId;
    private String title;
    private String description;
    private String images; // JSON array string or comma-separated URLs
    private String location;
    private BigDecimal price;
    private String brand;
    private String model;
    private Integer year;
    private String bodyType;
    private String color;
    private Integer mileage;
    private String inspection;
    private String origin;
    private Integer numberOfSeats;
    private String licensePlate;
    private String accessories;
    private Double batteryCapacity;
    private String condition;
    private String status;
    private LocalDateTime createdAt;
    private String sellerName;
    private String sellerPhone;

    // Constructors
    public VehicleResponse() {}

    public VehicleResponse(Long vehicleId, String title, String description, String images,
                          String location, BigDecimal price, String brand, String model,
                          Integer year, String bodyType, String color, Integer mileage,
                          String inspection, String origin, Integer numberOfSeats,
                          String licensePlate, String accessories, Double batteryCapacity,
                          String condition, String status, LocalDateTime createdAt,
                          String sellerName, String sellerPhone) {
        this.vehicleId = vehicleId;
        this.title = title;
        this.description = description;
        this.images = images;
        this.location = location;
        this.price = price;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.bodyType = bodyType;
        this.color = color;
        this.mileage = mileage;
        this.inspection = inspection;
        this.origin = origin;
        this.numberOfSeats = numberOfSeats;
        this.licensePlate = licensePlate;
        this.accessories = accessories;
        this.batteryCapacity = batteryCapacity;
        this.condition = condition;
        this.status = status;
        this.createdAt = createdAt;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
    }

    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getBodyType() { return bodyType; }
    public void setBodyType(String bodyType) { this.bodyType = bodyType; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public String getInspection() { return inspection; }
    public void setInspection(String inspection) { this.inspection = inspection; }
    
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    
    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public String getAccessories() { return accessories; }
    public void setAccessories(String accessories) { this.accessories = accessories; }
    
    public Double getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Double batteryCapacity) { this.batteryCapacity = batteryCapacity; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
}
