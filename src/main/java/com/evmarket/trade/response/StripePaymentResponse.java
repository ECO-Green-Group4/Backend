package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO trả về cho client sau khi tạo Stripe Payment Intent
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripePaymentResponse {

    /**
     * Client Secret - Key bí mật được tạo từ PaymentIntent
     * Client sử dụng key này để confirm thanh toán bằng Stripe.js
     * 
     * Lưu ý: Client secret khác với API secret key
     * Client secret chỉ valid cho 1 PaymentIntent cụ thể
     */
    private String clientSecret;

    /**
     * PaymentIntent ID - Unique identifier của PaymentIntent
     * Dùng để tracking, webhook, và refund
     */
    private String paymentIntentId;

    /**
     * Publishable Key - Key công khai của Stripe
     * Frontend cần key này để khởi tạo Stripe.js
     */
    private String publishableKey;

    /**
     * Số tiền thanh toán (VND)
     */
    private Long amount;

    /**
     * Đơn vị tiền tệ (vnd, usd, etc.)
     */
    private String currency;

    /**
     * Trạng thái của PaymentIntent
     * Possible values:
     * - requires_payment_method: Chưa có payment method
     * - requires_confirmation: Chờ xác nhận
     * - requires_action: Cần thêm action (3D Secure)
     * - processing: Đang xử lý
     * - succeeded: Thành công
     * - canceled: Đã hủy
     */
    private String status;

    /**
     * Mô tả giao dịch
     */
    private String description;

    /**
     * Order ID liên kết
     */
    private Long orderId;

    /**
     * Thông báo cho user
     */
    private String message;
}

