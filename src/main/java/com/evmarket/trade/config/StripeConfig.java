package com.evmarket.trade.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Stripe Payment Gateway
 * 
 * Stripe là một trong những payment gateway phổ biến nhất thế giới,
 * hỗ trợ thanh toán qua thẻ tín dụng/ghi nợ, ví điện tử, và nhiều phương thức khác.
 * 
 * Cấu hình này sử dụng Stripe Sandbox để test thanh toán mà không mất tiền thật.
 * 
 * @see <a href="https://stripe.com/docs">Stripe Documentation</a>
 */
@Configuration
public class StripeConfig {

    /**
     * Publishable Key - Key công khai dùng ở frontend
     * Dùng để khởi tạo Stripe.js và Elements
     */
    @Value("${stripe.publishable-key}")
    private String publishableKey;

    /**
     * Secret Key - Key bí mật chỉ dùng ở backend
     * Dùng để tạo PaymentIntent, xử lý thanh toán, refund, etc.
     * QUAN TRỌNG: KHÔNG BAO GIỜ expose key này ra frontend
     */
    @Value("${stripe.secret-key}")
    private String secretKey;

    /**
     * Webhook Secret - Dùng để xác thực webhook từ Stripe
     * Khi cấu hình webhook trên Stripe Dashboard, bạn sẽ nhận được secret này
     */
    @Value("${stripe.webhook-secret:#{null}}")
    private String webhookSecret;

    /**
     * Success URL - URL redirect khi thanh toán thành công
     */
    @Value("${stripe.success-url:http://localhost:3000/payment/success}")
    private String successUrl;

    /**
     * Cancel URL - URL redirect khi user hủy thanh toán
     */
    @Value("${stripe.cancel-url:http://localhost:3000/payment/cancel}")
    private String cancelUrl;

    /**
     * Currency - Đơn vị tiền tệ (VND, USD, EUR, etc.)
     */
    @Value("${stripe.currency:VND}")
    private String currency;

    /**
     * Khởi tạo Stripe API với secret key
     * Method này chạy sau khi Spring khởi tạo bean
     */
    @PostConstruct
    public void init() {
        Stripe.apiKey = this.secretKey;
    }

    // Getters
    public String getPublishableKey() {
        return publishableKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public String getCurrency() {
        return currency;
    }
}

