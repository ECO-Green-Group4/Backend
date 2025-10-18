package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType paymentType;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    // Reference fields (chỉ 1 trong các field này có giá trị)
    @Column(name = "listing_package_id")
    private Long listingPackageId;

    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "contract_addon_id")
    private Long contractAddOnId;

    // Common fields
    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    // Audit fields
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PaymentType {
        PACKAGE,      // Thanh toán gói tin VIP
        MEMBERSHIP,   // Thanh toán membership
        CONTRACT,     // Thanh toán hợp đồng
        ADDON         // Thanh toán addon
    }

    // Constructors
    public Payment() {}

    // Getters and Setters
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public String getPaymentGateway() { return paymentGateway; }
    public void setPaymentGateway(String paymentGateway) { this.paymentGateway = paymentGateway; }

    public String getGatewayTransactionId() { return gatewayTransactionId; }
    public void setGatewayTransactionId(String gatewayTransactionId) { this.gatewayTransactionId = gatewayTransactionId; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public Long getListingPackageId() { return listingPackageId; }
    public void setListingPackageId(Long listingPackageId) { this.listingPackageId = listingPackageId; }

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public Long getContractAddOnId() { return contractAddOnId; }
    public void setContractAddOnId(Long contractAddOnId) { this.contractAddOnId = contractAddOnId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDateTime getExpiryTime() { return expiryTime; }
    public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

    public User getPayer() { return payer; }
    public void setPayer(User payer) { this.payer = payer; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}