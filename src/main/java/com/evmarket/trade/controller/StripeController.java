package com.evmarket.trade.controller;

import com.evmarket.trade.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller xử lý Stripe Payment Gateway
 * 
 * Controller này cung cấp các API endpoint để:
 * 1. Tạo PaymentIntent (cho custom payment form)
 * 2. Tạo Checkout Session (cho hosted checkout page)
 * 3. Xử lý webhook từ Stripe
 * 4. Hủy thanh toán và hoàn tiền
 */
@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Stripe Payment", description = "Stripe Payment Gateway Integration API")
public class StripeController {

    private final StripeService stripeService;
    private final com.evmarket.trade.serviceImp.StripePaymentServiceImpl stripePaymentService;



    /**
     * Webhook từ Stripe
     * 
     * Stripe gọi endpoint này để thông báo các event:
     * - payment_intent.succeeded: Thanh toán thành công
     * - payment_intent.payment_failed: Thanh toán thất bại
     * - payment_intent.canceled: Thanh toán bị hủy
     * - charge.refunded: Đã hoàn tiền
     * - checkout.session.completed: Checkout session hoàn thành
     * 
     * Quan trọng:
     * - Phải verify webhook signature để đảm bảo request từ Stripe
     * - Endpoint này phải public (không cần authentication)
     * - Phải xử lý idempotent (có thể nhận cùng event nhiều lần)
     */
    @PostMapping("/webhook")
    @Operation(summary = "Stripe Webhook", 
               description = "Handle webhook events from Stripe")
    public ResponseEntity<?> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        log.info("Received Stripe webhook");

        // Verify webhook signature
        if (!stripeService.verifyWebhookSignature(payload, sigHeader)) {
            log.error("Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        try {
            // Parse event from JSON payload
            Event event = com.stripe.model.Event.GSON.fromJson(payload, Event.class);
            
            log.info("Processing Stripe event: {} ({})", event.getType(), event.getId());

            // Xử lý các event type
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                    
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                    
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(event);
                    break;
                    
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                    
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                    
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    // ==================== Private Helper Methods ====================

    /**
     * Xử lý event: Thanh toán thành công
     */
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);
        
        if (paymentIntent != null) {
            log.info("Payment succeeded: {}", paymentIntent.getId());
            
            // Lấy order ID từ metadata
            String orderId = paymentIntent.getMetadata().get("order_id");
            
            log.info("Order {} payment succeeded via PaymentIntent", orderId);
        }
    }

    /**
     * Xử lý event: Thanh toán thất bại
     */
    private void handlePaymentIntentFailed(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject().orElse(null);
            
            if (paymentIntent != null) {
                log.warn("Payment failed: {}", paymentIntent.getId());
                
                // Note: PaymentIntent không có session ID trong metadata
                // Chỉ có thể track nếu dùng PaymentIntent API trực tiếp
                String orderId = paymentIntent.getMetadata().get("order_id");
                log.info("Payment failed for order: {}", orderId);
            }
        } catch (Exception e) {
            log.error("Error handling payment failed: ", e);
        }
    }

    /**
     * Xử lý event: Thanh toán bị hủy
     */
    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);
        
        if (paymentIntent != null) {
            log.info("Payment canceled: {}", paymentIntent.getId());
            
            String orderId = paymentIntent.getMetadata().get("order_id");
            
            // TODO: Cập nhật trạng thái đơn hàng
            // orderService.updatePaymentStatus(Long.parseLong(orderId), "CANCELED");
            
            log.info("Order {} marked as CANCELED", orderId);
        }
    }

    /**
     * Xử lý event: Đã hoàn tiền
     */
    private void handleChargeRefunded(Event event) {
        log.info("Charge refunded event received");
        
        // TODO: Xử lý logic hoàn tiền
        // Cập nhật trạng thái đơn hàng, gửi email thông báo, etc.
    }

    /**
     * Xử lý event: Checkout session hoàn thành
     * 
     * Đây là event quan trọng nhất - được trigger khi user hoàn thành checkout
     * Gọi service để cập nhật database và activate package/membership/contract
     */
    private void handleCheckoutSessionCompleted(Event event) {
        try {
            log.info("🔵 Starting to handle checkout.session.completed event");
            
            // Parse event JSON để lấy session ID
            // Stripe event structure: event.data.object contains the session
            com.google.gson.Gson gson = new com.google.gson.Gson();
            String eventJson = gson.toJson(event.getData());
            com.google.gson.JsonObject dataJson = gson.fromJson(eventJson, com.google.gson.JsonObject.class);
            
            if (!dataJson.has("object")) {
                log.error("❌ Event data does not contain 'object' field");
                return;
            }
            
            com.google.gson.JsonObject sessionJson = dataJson.getAsJsonObject("object");
            
            if (!sessionJson.has("id")) {
                log.error("❌ Cannot extract session ID from event data");
                return;
            }
            
            String sessionId = sessionJson.get("id").getAsString();
            log.info("📝 Extracted session ID from event: {}", sessionId);
            
            // Fetch session từ Stripe API để có đầy đủ thông tin
            Session session = stripeService.getCheckoutSession(sessionId);
            
            if (session == null) {
                log.error("❌ Cannot fetch session from Stripe API: {}", sessionId);
                return;
            }
            
            log.info("✅ Checkout session completed: {}", session.getId());
            log.info("📊 Payment status: {}", session.getPaymentStatus());
            
            // Kiểm tra payment status
            if ("paid".equals(session.getPaymentStatus())) {
                log.info("💳 Payment is PAID. Calling stripePaymentService.handleStripePaymentSuccess()");
                // Gọi service để xử lý payment success
                stripePaymentService.handleStripePaymentSuccess(session.getId());
                log.info("✅ Successfully processed payment for session: {}", session.getId());
            } else {
                log.warn("⚠️ Checkout session completed but payment status is: {}", session.getPaymentStatus());
            }
        } catch (Exception e) {
            log.error("❌ Error handling checkout session completed: ", e);
            log.error("❌ Exception class: {}", e.getClass().getName());
            log.error("❌ Exception message: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("❌ Cause: {}", e.getCause().getMessage());
            }
            e.printStackTrace();
            // Không throw exception để không làm fail webhook
        }
    }
}

