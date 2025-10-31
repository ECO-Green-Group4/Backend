package com.evmarket.trade.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để tạo Stripe Payment Intent
 * 
 * PaymentIntent là đối tượng trung tâm trong Stripe API,
 * đại diện cho một giao dịch thanh toán từ đầu đến cuối.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentRequest {

    /**
     * ID của đơn hàng/giao dịch cần thanh toán
     * Dùng để liên kết PaymentIntent với đơn hàng trong hệ thống
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Số tiền thanh toán (VND)
     * Lưu ý: Stripe yêu cầu số tiền phải là số nguyên (không có phần thập phân)
     */
    @NotNull(message = "Amount is required")
    @Min(value = 1000, message = "Amount must be at least 1,000 VND")
    private Long amount;

    /**
     * Mô tả giao dịch (optional)
     * Hiển thị trên Stripe Dashboard và trong email gửi cho khách hàng
     */
    private String description;

    /**
     * Email của khách hàng (optional nhưng nên có)
     * Stripe sẽ gửi receipt đến email này
     */
    private String customerEmail;
}

