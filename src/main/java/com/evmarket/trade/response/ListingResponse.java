package com.evmarket.trade.response;

import com.evmarket.trade.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingResponse {
    private Long listingId;
    private User user;
    private String itemType;
    private String title;
    private String description;
    private String images;
    private String location;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    
    // Vehicle fields
    private String brand;
    private String model;
    private Integer year;
    private Double batteryCapacity;
    private Integer mileage;
    private String condition;
    
    // Battery fields
    private String type;
    private Double capacity;
    private Integer healthPercent;
    private Integer manufactureYear;

    // Constructors
    public ListingResponse() {}

    // Getters and Setters
    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    
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
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Vehicle getters/setters
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
    
    // Battery getters/setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    
    public Integer getHealthPercent() { return healthPercent; }
    public void setHealthPercent(Integer healthPercent) { this.healthPercent = healthPercent; }
    
    public Integer getManufactureYear() { return manufactureYear; }
    public void setManufactureYear(Integer manufactureYear) { this.manufactureYear = manufactureYear; }
}
