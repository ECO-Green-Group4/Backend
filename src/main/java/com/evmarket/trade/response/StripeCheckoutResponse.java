package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO trả về sau khi tạo Stripe Checkout Session
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeCheckoutResponse {

    /**
     * Checkout Session ID
     */
    private String sessionId;

    /**
     * URL của trang thanh toán Stripe
     * Redirect user đến URL này để thanh toán
     */
    private String checkoutUrl;

    /**
     * Publishable Key để khởi tạo Stripe.js (nếu cần)
     */
    private String publishableKey;

    /**
     * Order ID liên kết
     */
    private Long orderId;

    /**
     * Số tiền thanh toán
     */
    private Long amount;

    /**
     * Đơn vị tiền tệ
     */
    private String currency;

    /**
     * Thông báo cho user
     */
    private String message;
}

