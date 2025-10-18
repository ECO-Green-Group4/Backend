package com.evmarket.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
    @Value("${vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String payUrl;

    @Value("${vnpay.tmnCode}")
    private String tmnCode;  // e.g., DEMOV210

    @Value("${vnpay.hashSecret}")
    private String hashSecret;  // Secret key từ VNPay

    @Value("${vnpay.returnUrl:http://localhost:8080/api/payment/vnpay-return}")
    private String returnUrl;

    @Value("${vnpay.ipnUrl:http://localhost:8080/api/payment/vnpay-ipn}")
    private String ipnUrl;  // Server-to-server, cần public IP/SSL

    // Getters
    public String getPayUrl() { return payUrl; }
    public String getTmnCode() { return tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public String getReturnUrl() { return returnUrl; }
    public String getIpnUrl() { return ipnUrl; }
}
