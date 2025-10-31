package com.evmarket.trade.request;

import java.math.BigDecimal;

public class UpdateAddOnServiceRequest {
    private String name;
    private String description;
    private BigDecimal fee;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}




