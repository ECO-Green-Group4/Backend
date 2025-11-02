package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContractAddOnResponse {
    private Long id;
    private Long contractId;
    private Long serviceId;
    private String serviceName;
    private BigDecimal fee;
    private LocalDateTime createdAt;
    private String paymentStatus;
    private String chargedTo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getChargedTo() { return chargedTo; }
    public void setChargedTo(String chargedTo) { this.chargedTo = chargedTo; }
}




