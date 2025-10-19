package com.evmarket.trade.request;

import lombok.Data;

@Data
public class MomoCallbackRequest {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Integer amount;
    private String orderInfo;
    private String orderType;
    private String transId;
    private Integer resultCode;
    private String message;
    private String payType;
    private String responseTime;
    private String extraData;
    private String signature;

    public boolean isSuccess() {
        return resultCode != null && resultCode == 0;
    }

    public String getGatewayTransactionId() {
        return transId;
    }
}