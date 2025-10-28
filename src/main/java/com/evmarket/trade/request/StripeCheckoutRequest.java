package com.evmarket.trade.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để tạo Stripe Checkout Session
 * 
 * Checkout Session tạo một trang thanh toán hosted của Stripe,
 * user sẽ được redirect đến trang này để nhập thông tin thẻ.
 * 
 * Đây là cách đơn giản nhất để tích hợp Stripe,
 * không cần xây dựng form thanh toán custom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripeCheckoutRequest {

    /**
     * ID của đơn hàng cần thanh toán
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Số tiền thanh toán (VND)
     */
    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Amount must be at least 1,000 VND")
    private Long amount;

    /**
     * Tên sản phẩm/dịch vụ
     */
    @NotNull(message = "Product name is required")
    private String productName;

    /**
     * Mô tả sản phẩm/dịch vụ
     */
    private String description;

    /**
     * Email của khách hàng
     */
    private String customerEmail;

    /**
     * Số lượng sản phẩm (mặc định = 1)
     */
    private Integer quantity = 1;
}

