package com.evmarket.trade.serviceImp;

import com.evmarket.trade.config.StripeConfig;
import com.evmarket.trade.request.StripeCheckoutRequest;
import com.evmarket.trade.request.StripePaymentRequest;
import com.evmarket.trade.response.StripeCheckoutResponse;
import com.evmarket.trade.response.StripePaymentResponse;
import com.evmarket.trade.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCancelParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation của StripeService
 * 
 * Service này sử dụng Stripe Java SDK để tương tác với Stripe API.
 * Stripe API key được cấu hình tự động trong StripeConfig.init()
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeServiceImpl implements StripeService {

    private final StripeConfig stripeConfig;

    @Override
    public StripePaymentResponse createPaymentIntent(StripePaymentRequest request) throws StripeException {
        log.info("Creating Stripe PaymentIntent for order: {}, amount: {} VND", 
                request.getOrderId(), request.getAmount());

        // Tạo metadata để lưu thông tin đơn hàng
        // Metadata sẽ được trả về trong webhook và có thể query
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", String.valueOf(request.getOrderId()));
        metadata.put("integration_source", "ev_trade_backend");

        // Build PaymentIntent params
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount()) // Amount in smallest currency unit (VND không có đơn vị nhỏ hơn)
                .setCurrency(stripeConfig.getCurrency().toLowerCase()) // vnd, usd, etc.
                .putAllMetadata(metadata)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                );

        // Thêm description nếu có
        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            paramsBuilder.setDescription(request.getDescription());
        }

        // Thêm customer email nếu có (để Stripe gửi receipt)
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            paramsBuilder.setReceiptEmail(request.getCustomerEmail());
        }

        // Tạo PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

        log.info("Created PaymentIntent: {} with status: {}", 
                paymentIntent.getId(), paymentIntent.getStatus());

        // Build response
        return StripePaymentResponse.builder()
                .clientSecret(paymentIntent.getClientSecret())
                .paymentIntentId(paymentIntent.getId())
                .publishableKey(stripeConfig.getPublishableKey())
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency())
                .status(paymentIntent.getStatus())
                .description(paymentIntent.getDescription())
                .orderId(request.getOrderId())
                .message("Payment Intent created successfully. Use client secret to confirm payment.")
                .build();
    }

    @Override
    public StripeCheckoutResponse createCheckoutSession(StripeCheckoutRequest request) throws StripeException {
        log.info("Creating Stripe Checkout Session for order: {}, amount: {} VND", 
                request.getOrderId(), request.getAmount());

        // Tạo metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", String.valueOf(request.getOrderId()));
        metadata.put("integration_source", "ev_trade_backend");

        // Build line items (sản phẩm trong giỏ hàng)
        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(stripeConfig.getCurrency().toLowerCase())
                                .setUnitAmount(request.getAmount()) // Giá mỗi đơn vị
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName(request.getProductName())
                                                .setDescription(request.getDescription())
                                                .build()
                                )
                                .build()
                )
                .setQuantity(request.getQuantity() != null ? request.getQuantity().longValue() : 1L)
                .build();

        // Build checkout session params
        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT) // Mode PAYMENT cho thanh toán 1 lần
                .setSuccessUrl(stripeConfig.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(stripeConfig.getCancelUrl())
                .addLineItem(lineItem)
                .putAllMetadata(metadata);

        // Thêm customer email nếu có
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
            paramsBuilder.setCustomerEmail(request.getCustomerEmail());
        }

        // Tạo Checkout Session
        Session session = Session.create(paramsBuilder.build());

        log.info("Created Checkout Session: {} with URL: {}", 
                session.getId(), session.getUrl());

        // Build response
        return StripeCheckoutResponse.builder()
                .sessionId(session.getId())
                .checkoutUrl(session.getUrl())
                .publishableKey(stripeConfig.getPublishableKey())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(stripeConfig.getCurrency())
                .message("Checkout session created. Redirect user to checkout URL.")
                .build();
    }

    @Override
    public PaymentIntent getPaymentIntent(String paymentIntentId) throws StripeException {
        log.info("Retrieving PaymentIntent: {}", paymentIntentId);
        return PaymentIntent.retrieve(paymentIntentId);
    }

    @Override
    public Session getCheckoutSession(String sessionId) throws StripeException {
        log.info("Retrieving Checkout Session: {}", sessionId);
        return Session.retrieve(sessionId);
    }

    @Override
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        log.info("Canceling PaymentIntent: {}", paymentIntentId);
        
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        PaymentIntentCancelParams params = PaymentIntentCancelParams.builder()
                .setCancellationReason(PaymentIntentCancelParams.CancellationReason.REQUESTED_BY_CUSTOMER)
                .build();
        
        PaymentIntent canceled = paymentIntent.cancel(params);
        
        log.info("PaymentIntent {} canceled successfully", paymentIntentId);
        return canceled;
    }

    @Override
    public Refund createRefund(String paymentIntentId, Long amount, String reason) throws StripeException {
        log.info("Creating refund for PaymentIntent: {}, amount: {}, reason: {}", 
                paymentIntentId, amount, reason);

        RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId);

        // Nếu có amount thì refund một phần, không thì refund toàn bộ
        if (amount != null) {
            paramsBuilder.setAmount(amount);
        }

        // Set reason nếu có
        if (reason != null && !reason.isEmpty()) {
            RefundCreateParams.Reason reasonEnum;
            try {
                reasonEnum = RefundCreateParams.Reason.valueOf(reason.toUpperCase());
                paramsBuilder.setReason(reasonEnum);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid refund reason: {}, using default", reason);
            }
        }

        Refund refund = Refund.create(paramsBuilder.build());

        log.info("Created refund: {} with status: {}", refund.getId(), refund.getStatus());
        return refund;
    }

    @Override
    public boolean verifyWebhookSignature(String payload, String sigHeader) {
        if (stripeConfig.getWebhookSecret() == null || stripeConfig.getWebhookSecret().isEmpty()) {
            log.warn("Webhook secret is not configured. Skipping signature verification.");
            return false; // ✅ PRODUCTION: Reject webhooks without proper verification
        }

        try {
            Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
            log.info("Webhook signature verified successfully");
            return true;
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            return false;
        }
    }
}

