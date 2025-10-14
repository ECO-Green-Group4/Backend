package com.evmarket.trade.response;

import java.time.LocalDateTime;

public class ContractResponse {
    private Long contractId;
    private Long orderId;
    private String contractFile;
    private Boolean signedBySeller;
    private Boolean signedByBuyer;
    private LocalDateTime signedAt;
    private String contractStatus;
    private String buyerName;
    private String sellerName;
    private String listingTitle;

    // Constructors
    public ContractResponse() {}

    public ContractResponse(Long contractId, Long orderId, String contractFile,
                          Boolean signedBySeller, Boolean signedByBuyer, LocalDateTime signedAt,
                          String contractStatus, String buyerName, String sellerName, String listingTitle) {
        this.contractId = contractId;
        this.orderId = orderId;
        this.contractFile = contractFile;
        this.signedBySeller = signedBySeller;
        this.signedByBuyer = signedByBuyer;
        this.signedAt = signedAt;
        this.contractStatus = contractStatus;
        this.buyerName = buyerName;
        this.sellerName = sellerName;
        this.listingTitle = listingTitle;
    }

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
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
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public String getListingTitle() { return listingTitle; }
    public void setListingTitle(String listingTitle) { this.listingTitle = listingTitle; }
}
