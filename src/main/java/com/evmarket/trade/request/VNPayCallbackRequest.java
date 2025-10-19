package com.evmarket.trade.request;

import lombok.Data;

@Data
public class VNPayCallbackRequest {
    // Required fields
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_BankTranNo;
    private String vnp_CardType;
    private String vnp_OrderInfo;
    private String vnp_PayDate;
    private String vnp_ResponseCode;
    private String vnp_TmnCode;
    private String vnp_TransactionNo;
    private String vnp_TransactionStatus;
    private String vnp_TxnRef;
    private String vnp_SecureHash;

    // Optional fields
    private String vnp_Version;
    private String vnp_Command;
    private String vnp_CurrCode;
    private String vnp_Locale;
    private String vnp_ReturnUrl;
    private String vnp_IpAddr;
    private String vnp_CreateDate;
    private String vnp_OrderType;
    private String vnp_SecureHashType;
}