package com.evmarket.trade.service;

import com.evmarket.trade.request.StripeCheckoutRequest;
import com.evmarket.trade.request.StripePaymentRequest;
import com.evmarket.trade.response.StripeCheckoutResponse;
import com.evmarket.trade.response.StripePaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;

/**
 * Service interface cho Stripe Payment Gateway
 * 
 * Stripe hỗ trợ nhiều cách tích hợp thanh toán:
 * 1. Payment Intents API - Tích hợp custom, kiểm soát hoàn toàn UI
 * 2. Checkout Session - Trang thanh toán hosted, dễ tích hợp
 * 3. Payment Links - Tạo link thanh toán nhanh
 * 
 * Service này triển khai 2 cách đầu tiên.
 */
public interface StripeService {

    /**
     * Tạo Payment Intent để xử lý thanh toán
     * 
     * Payment Intent đại diện cho toàn bộ vòng đời thanh toán,
     * từ khi khởi tạo đến khi hoàn thành hoặc thất bại.
     * 
     * Flow:
     * 1. Backend tạo PaymentIntent với secret key
     * 2. Backend trả về client secret cho frontend
     * 3. Frontend dùng client secret + publishable key để confirm thanh toán
     * 4. Stripe xử lý thanh toán và gọi webhook (nếu có)
     * 
     * @param request Thông tin thanh toán
     * @return Response chứa client secret và payment intent ID
     * @throws StripeException Nếu có lỗi khi gọi Stripe API
     */
    StripePaymentResponse createPaymentIntent(StripePaymentRequest request) throws StripeException;

    /**
     * Tạo Checkout Session để redirect user đến trang thanh toán
     * 
     * Checkout Session là cách đơn giản nhất để tích hợp Stripe.
     * Stripe cung cấp sẵn trang thanh toán, bạn chỉ cần redirect user đến đó.
     * 
     * Ưu điểm:
     * - Không cần xây dựng UI form thanh toán
     * - Stripe tự động handle 3D Secure, SCA compliance
     * - Hỗ trợ nhiều payment method (card, wallet, etc.)
     * 
     * @param request Thông tin thanh toán và sản phẩm
     * @return Response chứa URL trang thanh toán
     * @throws StripeException Nếu có lỗi khi gọi Stripe API
     */
    StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request) throws StripeException;

    /**
     * Lấy thông tin chi tiết của Payment Intent
     * 
     * Dùng để kiểm tra trạng thái thanh toán, xác minh webhook, etc.
     * 
     * @param paymentIntentId ID của Payment Intent
     * @return PaymentIntent object từ Stripe
     * @throws StripeException Nếu có lỗi khi gọi Stripe API
     */
    PaymentIntent getPaymentIntent(String paymentIntentId) throws StripeException;

    /**
     * Lấy thông tin chi tiết của Checkout Session
     * 
     * @param sessionId ID của Checkout Session
     * @return Session object từ Stripe
     * @throws StripeException Nếu có lỗi khi gọi Stripe API
     */
    Session getCheckoutSession(String sessionId) throws StripeException;

    /**
     * Hủy Payment Intent
     * 
     * Chỉ có thể hủy khi Payment Intent đang ở trạng thái:
     * - requires_payment_method
     * - requires_capture
     * - requires_confirmation
     * - requires_action
     * 
     * @param paymentIntentId ID của Payment Intent cần hủy
     * @return PaymentIntent đã hủy
     * @throws StripeException Nếu có lỗi hoặc không thể hủy
     */
    PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException;

    /**
     * Hoàn tiền (refund) cho một giao dịch đã thành công
     * 
     * @param paymentIntentId ID của Payment Intent đã thanh toán thành công
     * @param amount Số tiền hoàn (nếu null thì hoàn toàn bộ)
     * @param reason Lý do hoàn tiền (duplicate, fraudulent, requested_by_customer)
     * @return Refund object từ Stripe
     * @throws StripeException Nếu có lỗi khi hoàn tiền
     */
    Refund createRefund(String paymentIntentId, Long amount, String reason) throws StripeException;

    /**
     * Xác thực webhook signature từ Stripe
     * 
     * Stripe ký mỗi webhook event bằng webhook secret.
     * Cần verify signature để đảm bảo webhook thực sự từ Stripe.
     * 
     * @param payload Webhook payload (raw JSON string)
     * @param sigHeader Stripe-Signature header
     * @return true nếu signature hợp lệ
     */
    boolean verifyWebhookSignature(String payload, String sigHeader);
}

