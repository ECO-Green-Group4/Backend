package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "addonservices")
public class AddOnService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "default_fee")
    private BigDecimal defaultFee;

    @Column(name = "status")
    private String status;

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getDefaultFee() { return defaultFee; }
    public void setDefaultFee(BigDecimal defaultFee) { this.defaultFee = defaultFee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}