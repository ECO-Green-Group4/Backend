package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CreateVehicleListingRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    private String images;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    // Vehicle specific fields
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;
    
    @NotNull(message = "Battery capacity is required")
    @Positive(message = "Battery capacity must be positive")
    private Double batteryCapacity;
    
    @NotNull(message = "Mileage is required")
    @Positive(message = "Mileage must be positive")
    private Integer mileage;
    
    @NotBlank(message = "Condition is required")
    private String condition;

    // Getters and Setters
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
    
    public Double getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Double batteryCapacity) { this.batteryCapacity = batteryCapacity; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
