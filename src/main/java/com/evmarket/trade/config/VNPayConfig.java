package com.evmarket.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {

    // VNPay Payment URL
    @Value("${vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    // VNPay API URL for refund/query
    @Value("${vnpay.api-url:https://sandbox.vnpayment.vn/merchant_webapi/api/transaction}")
    private String vnpApiUrl;

    // Terminal Code (TMN Code) - Merchant website code
    @Value("${vnpay.tmn-code:70D2G744}")
    private String vnpTmnCode;

    // Hash Secret - Secret key for checksum generation
    @Value("${vnpay.hash-secret:SAN3L9QM95TW3PXNMZ8HLEFKLDYJVMBP}")
    private String vnpHashSecret;

    // Return URL - VNPay redirects to this URL after payment
    @Value("${vnpay.return-url:http://localhost:8080/api/payments/vnpay-callback}")
    private String vnpReturnUrl;

    // Version
    @Value("${vnpay.version:2.1.0}")
    private String vnpVersion;

    // Command (default: pay)
    @Value("${vnpay.command:pay}")
    private String vnpCommand;

    // Order Type
    @Value("${vnpay.order-type:other}")
    private String vnpOrderType;

    // Locale (vn or en)
    @Value("${vnpay.locale:vn}")
    private String vnpLocale;

    // Currency Code (VND)
    @Value("${vnpay.currency-code:VND}")
    private String vnpCurrencyCode;

    // Getters
    public String getVnpPayUrl() {
        return vnpPayUrl;
    }

    public String getVnpApiUrl() {
        return vnpApiUrl;
    }

    public String getVnpTmnCode() {
        return vnpTmnCode;
    }

    public String getVnpHashSecret() {
        return vnpHashSecret;
    }

    public String getVnpReturnUrl() {
        return vnpReturnUrl;
    }

    public String getVnpVersion() {
        return vnpVersion;
    }

    public String getVnpCommand() {
        return vnpCommand;
    }

    public String getVnpOrderType() {
        return vnpOrderType;
    }

    public String getVnpLocale() {
        return vnpLocale;
    }

    public String getVnpCurrencyCode() {
        return vnpCurrencyCode;
    }
}