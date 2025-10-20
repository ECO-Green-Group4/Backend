package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class CreateBatteryRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotBlank(message = "Battery brand is required")
    private String batteryBrand;
    
    @NotNull(message = "Voltage is required")
    @Positive(message = "Voltage must be positive")
    private Double voltage;
    
    @NotBlank(message = "Capacity is required")
    private String capacity; // "32Ah or 2000Wh" - keep as String
    
    @NotNull(message = "Health percent is required")
    @Min(value = 0, message = "Health percent must be at least 0")
    @Max(value = 100, message = "Health percent must be at most 100")
    private Integer healthPercent;
    
    @NotNull(message = "Charge cycles is required")
    @Positive(message = "Charge cycles must be positive")
    private Integer chargeCycles;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Manufacture year is required")
    @Positive(message = "Manufacture year must be positive")
    private Integer manufactureYear;
    
    @NotBlank(message = "Origin is required")
    private String origin;

    // Getters and Setters
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
}
