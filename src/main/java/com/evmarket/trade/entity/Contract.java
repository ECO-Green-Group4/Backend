package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "contract_file", columnDefinition = "nvarchar")
    private String contractFile;

    @Column(name = "signed_by_seller")
    private Boolean signedBySeller;

    @Column(name = "signed_by_buyer")
    private Boolean signedByBuyer;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "contract_status", columnDefinition = "nvarchar")
    private String contractStatus;

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public String getContractFile() { return contractFile; }
    public void setContractFile(String contractFile) { this.contractFile = contractFile; }
    public Boolean getSignedBySeller() { return signedBySeller; }
    public void setSignedBySeller(Boolean signedBySeller) { this.signedBySeller = signedBySeller; }
    public Boolean getSignedByBuyer() { return signedByBuyer; }
    public void setSignedByBuyer(Boolean signedByBuyer) { this.signedByBuyer = signedByBuyer; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public String getContractStatus() { return contractStatus; }
    public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }
}