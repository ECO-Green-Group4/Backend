package com.evmarket.trade.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {
    private Long orderId;
    private Long listingId;
    private String listingTitle;
    private String itemType;
    private String buyerName;
    private String buyerPhone;
    private String sellerName;
    private String sellerPhone;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal basePrice;
    private BigDecimal commissionFee;
    private BigDecimal totalAmount;

    // Constructors
    public OrderResponse() {}

    public OrderResponse(Long orderId, Long listingId, String listingTitle, String itemType,
                        String buyerName, String buyerPhone, String sellerName, String sellerPhone,
                        LocalDateTime orderDate, String status, BigDecimal basePrice,
                        BigDecimal commissionFee, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.listingId = listingId;
        this.listingTitle = listingTitle;
        this.itemType = itemType;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.orderDate = orderDate;
        this.status = status;
        this.basePrice = basePrice;
        this.commissionFee = commissionFee;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }
    
    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }
    
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public String getBuyerPhone() { return buyerPhone; }
    public void setBuyerPhone(String buyerPhone) { this.buyerPhone = buyerPhone; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    
    public BigDecimal getCommissionFee() { return commissionFee; }
    public void setCommissionFee(BigDecimal commissionFee) { this.commissionFee = commissionFee; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
