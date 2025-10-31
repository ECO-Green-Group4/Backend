package com.evmarket.trade.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity để lưu thông tin giao dịch ngân hàng từ SePay webhook
 * Tương ứng với tb_transactions trong hướng dẫn SePay
 */
@Entity
@Table(name = "bank_transactions")
public class BankTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Gateway/Bank code (VD: MB, ACB, VCB...)
     */
    @Column(name = "gateway", length = 100, nullable = false)
    private String gateway;

    /**
     * Ngày giờ giao dịch từ ngân hàng
     */
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    /**
     * Số tài khoản ngân hàng nhận tiền
     */
    @Column(name = "account_number", length = 100)
    private String accountNumber;

    /**
     * Sub account (nếu có)
     */
    @Column(name = "sub_account", length = 250)
    private String subAccount;

    /**
     * Số tiền nhận vào
     */
    @Column(name = "amount_in", precision = 20, scale = 2, nullable = false)
    private BigDecimal amountIn;

    /**
     * Số tiền chuyển ra
     */
    @Column(name = "amount_out", precision = 20, scale = 2, nullable = false)
    private BigDecimal amountOut;

    /**
     * Số dư tích lũy
     */
    @Column(name = "accumulated", precision = 20, scale = 2, nullable = false)
    private BigDecimal accumulated;

    /**
     * Mã giao dịch từ ngân hàng
     */
    @Column(name = "code", length = 250)
    private String code;

    /**
     * Nội dung chuyển khoản
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Số tham chiếu (reference number)
     */
    @Column(name = "reference_number", length = 250)
    private String referenceNumber;

    /**
     * Body từ webhook (JSON)
     */
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    /**
     * ID của Payment tương ứng (nếu đã match)
     */
    @Column(name = "payment_id")
    private Long paymentId;

    /**
     * Trạng thái xử lý: PENDING, MATCHED, PROCESSED, IGNORED
     */
    @Column(name = "status", length = 50)
    private String status;

    /**
     * Thời gian tạo record
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BankTransaction() {
        this.amountIn = BigDecimal.ZERO;
        this.amountOut = BigDecimal.ZERO;
        this.accumulated = BigDecimal.ZERO;
        this.status = "PENDING";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
    }

    public BigDecimal getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(BigDecimal amountIn) {
        this.amountIn = amountIn;
    }

    public BigDecimal getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(BigDecimal amountOut) {
        this.amountOut = amountOut;
    }

    public BigDecimal getAccumulated() {
        return accumulated;
    }

    public void setAccumulated(BigDecimal accumulated) {
        this.accumulated = accumulated;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}


