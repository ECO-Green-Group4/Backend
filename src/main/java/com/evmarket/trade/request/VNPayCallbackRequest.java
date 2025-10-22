package com.evmarket.trade.request;

import lombok.Data;

@Data
public class VNPayCallbackRequest {
    
    // Merchant website code
    private String vnp_TmnCode;
    
    // Payment amount (VND * 100)
    private Long vnp_Amount;
    
    // Bank code
    private String vnp_BankCode;
    
    // Bank transaction number
    private String vnp_BankTranNo;
    
    // Card type
    private String vnp_CardType;
    
    // Order information
    private String vnp_OrderInfo;
    
    // Payment date
    private String vnp_PayDate;
    
    // Payment result response code
    private String vnp_ResponseCode;
    
    // Transaction number at VNPay
    private String vnp_TransactionNo;
    
    // Transaction reference at merchant system
    private String vnp_TxnRef;
    
    // Checksum
    private String vnp_SecureHash;
    
    // Transaction status
    private String vnp_TransactionStatus;
    
    // Version
    private String vnp_Version;
    
    // Invoice identifier
    private String vnp_InvoiceNo;
    
    // Customer's IP address
    private String vnp_IpAddr;
    
    // Currency code
    private String vnp_CurrencyCode;

    // Custom field - Payment ID in our database
    private Long paymentId;

    /**
     * Check if transaction is successful
     */
    public boolean isSuccess() {
        return "00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus);
    }

    /**
     * Get message from response code
     */
    public String getResponseMessage() {
        if (vnp_ResponseCode == null) {
            return "Unknown error";
        }
        
        return switch (vnp_ResponseCode) {
            case "00" -> "Transaction successful";
            case "07" -> "Transaction successful. Suspected fraud (related to fraud, unusual transaction)";
            case "09" -> "Transaction failed: Customer's card/account has not registered for Internet Banking service at the bank";
            case "10" -> "Transaction failed: Customer authenticated card/account information incorrectly more than 3 times";
            case "11" -> "Transaction failed: Payment timeout. Please try again";
            case "12" -> "Transaction failed: Customer's card/account is locked";
            case "13" -> "Transaction failed: Customer entered wrong transaction authentication password (OTP)";
            case "24" -> "Transaction failed: Customer canceled the transaction";
            case "51" -> "Transaction failed: Your account does not have sufficient balance to complete the transaction";
            case "65" -> "Transaction failed: Your account has exceeded the daily transaction limit";
            case "75" -> "Payment bank is under maintenance";
            case "79" -> "Transaction failed: Customer entered wrong payment password too many times";
            default -> "Transaction failed";
        };
    }
}

