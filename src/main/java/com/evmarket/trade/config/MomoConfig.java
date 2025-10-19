package com.evmarket.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoConfig {

    // MoMo API URL
    @Value("${momo.url:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String payUrl;

    // Partner Code - Lấy từ MoMo Business Portal
    @Value("${momo.partnerCode:MOMO}")
    private String partnerCode;

    // Access Key - Lấy từ MoMo Business Portal
    @Value("${momo.accessKey:F8BBA842ECF85}")
    private String accessKey;

    // Secret Key - Lấy từ MoMo Business Portal (dùng để tạo signature)
    @Value("${momo.secretKey:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;

    // Return URL - MoMo redirect user về URL này sau khi thanh toán
    @Value("${momo.return-url:http://localhost:8080/api/payments/momo-callback}")
    private String returnUrl;

    // IPN URL - MoMo gọi URL này để thông báo kết quả (server-to-server)
    @Value("${momo.ipn-url:http://localhost:8080/api/payments/momo-ipn}")
    private String ipnUrl;

    // Request Type:
    // - captureWallet: Mở ví MoMo
    // - payWithMethod: Hiển thị trang thanh toán với QR code
    @Value("${momo.request-type:payWithMethod}")
    private String requestType;

    // Ngôn ngữ (vi hoặc en)
    @Value("${momo.lang:vi}")
    private String lang;

    // Getters
    public String getPayUrl() { return payUrl; }
    public String getPartnerCode() { return partnerCode; }
    public String getAccessKey() { return accessKey; }
    public String getSecretKey() { return secretKey; }
    public String getReturnUrl() { return returnUrl; }
    public String getIpnUrl() { return ipnUrl; }
    public String getRequestType() { return requestType; }
    public String getLang() { return lang; }
}