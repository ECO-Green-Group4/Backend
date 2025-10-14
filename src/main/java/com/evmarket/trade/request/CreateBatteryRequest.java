package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public class CreateBatteryRequest {
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Double capacity;
    
    @NotNull(message = "Health percent is required")
    @Min(value = 0, message = "Health percent must be at least 0")
    @Max(value = 100, message = "Health percent must be at most 100")
    private Integer healthPercent;
    
    @NotNull(message = "Manufacture year is required")
    @Positive(message = "Manufacture year must be positive")
    private Integer manufactureYear;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    // Getters and Setters
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
}
