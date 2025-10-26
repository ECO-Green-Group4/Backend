package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.response.StripeCheckoutResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.StripeService;
import com.evmarket.trade.request.StripeCheckoutRequest;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service để xử lý thanh toán Stripe tích hợp với business logic của hệ thống
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentServiceImpl {

    private final StripeService stripeService;
    private final PaymentRepository paymentRepository;
    private final ListingPackageRepository listingPackageRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ContractRepository contractRepository;
    private final ContractAddOnRepository contractAddOnRepository;
    private final UserRepository userRepository;

    /**
     * Thanh toán Listing Package (VIP Package) bằng Stripe
     */
    @Transactional
    public BaseResponse<StripeCheckoutResponse> payListingPackageWithStripe(Long listingPackageId, User payer) {
        try {
            log.info("Starting Stripe payment for listing package: {}, user: {}", listingPackageId, payer.getUserId());

            // Validate listing package
            ListingPackage listingPackage = listingPackageRepository.findById(listingPackageId)
                    .orElseThrow(() -> new AppException("Listing package not found"));

            if (listingPackage.getListing().getUser().getUserId() != payer.getUserId()) {
                throw new AppException("You are not authorized to pay for this package");
            }

            if (listingPackage.getServicePackage().getPackageType() != ServicePackage.PackageType.LISTING_VIP) {
                throw new AppException("This is not a VIP package");
            }

            if (!"PENDING_PAYMENT".equals(listingPackage.getStatus())) {
                throw new AppException("Listing package is not in pending payment status");
            }

            // Check if payment already succeeded
            List<Payment> successPayments = paymentRepository.findByListingPackageIdAndPaymentStatus(
                    listingPackageId, "SUCCESS");
            if (!successPayments.isEmpty()) {
                throw new AppException("This package has already been paid");
            }

            BigDecimal amount = listingPackage.getServicePackage().getListingFee();
            
            // Convert BigDecimal to VND Long (Stripe yêu cầu amount là số nguyên)
            Long amountInVND = amount.multiply(BigDecimal.valueOf(1)).longValue();

            // Tạo Payment record với status PENDING
            Payment payment = new Payment();
            payment.setPaymentType(Payment.PaymentType.PACKAGE);
            payment.setListingPackageId(listingPackageId);
            payment.setPayer(payer);
            payment.setPaymentGateway("STRIPE");
            payment.setAmount(amount);
            payment.setCurrency("VND");
            payment.setPaymentStatus("PENDING");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setExpiryTime(LocalDateTime.now().plusMinutes(30)); // Payment expires in 30 minutes

            Payment savedPayment = paymentRepository.save(payment);

            // Tạo Stripe Checkout Session
            StripeCheckoutRequest stripeRequest = new StripeCheckoutRequest();
            stripeRequest.setOrderId(savedPayment.getPaymentId());
            stripeRequest.setAmount(amountInVND);
            stripeRequest.setProductName("VIP Package - " + listingPackage.getServicePackage().getName());
            stripeRequest.setDescription("Listing: " + listingPackage.getListing().getTitle());
            stripeRequest.setCustomerEmail(payer.getEmail());
            stripeRequest.setQuantity(1);

            StripeCheckoutResponse stripeResponse = stripeService.createCheckoutSession(stripeRequest);

            // Lưu Stripe Session ID vào payment
            payment.setGatewayTransactionId(stripeResponse.getSessionId());
            paymentRepository.save(payment);

            log.info("Created Stripe checkout session: {} for payment: {}", 
                    stripeResponse.getSessionId(), savedPayment.getPaymentId());

            return BaseResponse.success(stripeResponse, 
                    "Stripe checkout session created. Redirect user to checkout URL");

        } catch (StripeException e) {
            log.error("Stripe API error: ", e);
            throw new AppException("Failed to create Stripe payment: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing Stripe payment: ", e);
            throw new AppException("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * Thanh toán Membership bằng Stripe
     */
    @Transactional
    public BaseResponse<StripeCheckoutResponse> payMembershipWithStripe(Long servicePackageId, User payer) {
        try {
            log.info("Starting Stripe payment for membership: {}, user: {}", servicePackageId, payer.getUserId());

            ServicePackage servicePackage = servicePackageRepository.findById(servicePackageId)
                    .orElseThrow(() -> new AppException("Service package not found"));

            if (servicePackage.getPackageType() != ServicePackage.PackageType.MEMBERSHIP) {
                throw new AppException("This is not a membership package");
            }

            if (!"ACTIVE".equals(servicePackage.getStatus())) {
                throw new AppException("Service package is not available");
            }

            BigDecimal amount = servicePackage.getListingFee();
            Long amountInVND = amount.multiply(BigDecimal.valueOf(1)).longValue();

            // Tạo Payment record
            Payment payment = new Payment();
            payment.setPaymentType(Payment.PaymentType.MEMBERSHIP);
            payment.setPayer(payer);
            payment.setPaymentGateway("STRIPE");
            payment.setAmount(amount);
            payment.setCurrency("VND");
            payment.setPaymentStatus("PENDING");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setExpiryTime(LocalDateTime.now().plusMinutes(30));

            Payment savedPayment = paymentRepository.save(payment);

            // Tạo Stripe Checkout Session
            StripeCheckoutRequest stripeRequest = new StripeCheckoutRequest();
            stripeRequest.setOrderId(savedPayment.getPaymentId());
            stripeRequest.setAmount(amountInVND);
            stripeRequest.setProductName("Membership - " + servicePackage.getName());
            stripeRequest.setDescription(servicePackage.getDescription());
            stripeRequest.setCustomerEmail(payer.getEmail());
            stripeRequest.setQuantity(1);

            StripeCheckoutResponse stripeResponse = stripeService.createCheckoutSession(stripeRequest);

            payment.setGatewayTransactionId(stripeResponse.getSessionId());
            paymentRepository.save(payment);

            return BaseResponse.success(stripeResponse, "Membership payment session created");

        } catch (StripeException e) {
            log.error("Stripe API error: ", e);
            throw new AppException("Failed to create Stripe payment: " + e.getMessage());
        }
    }

    /**
     * Thanh toán Contract Add-On bằng Stripe
     */
    @Transactional
    public BaseResponse<StripeCheckoutResponse> payContractAddOnWithStripe(Long contractAddOnId, User payer) {
        try {
            log.info("Starting Stripe payment for contract add-on: {}, user: {}", contractAddOnId, payer.getUserId());

            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));

            Contract contract = contractAddOn.getContract();
            boolean isAuthorized = contract.getOrder().getBuyer().getUserId() == payer.getUserId()
                    || contract.getOrder().getSeller().getUserId() == payer.getUserId();

            if (!isAuthorized) {
                throw new AppException("You are not authorized to pay for this service");
            }

            BigDecimal amount = contractAddOn.getFee();
            Long amountInVND = amount.multiply(BigDecimal.valueOf(1)).longValue();

            // Tạo Payment record
            Payment payment = new Payment();
            payment.setPaymentType(Payment.PaymentType.ADDON);
            payment.setContractAddOnId(contractAddOnId);
            payment.setPayer(payer);
            payment.setPaymentGateway("STRIPE");
            payment.setAmount(amount);
            payment.setCurrency("VND");
            payment.setPaymentStatus("PENDING");
            payment.setCreatedAt(LocalDateTime.now());
            payment.setExpiryTime(LocalDateTime.now().plusMinutes(30));

            Payment savedPayment = paymentRepository.save(payment);

            // Tạo Stripe Checkout Session
            StripeCheckoutRequest stripeRequest = new StripeCheckoutRequest();
            stripeRequest.setOrderId(savedPayment.getPaymentId());
            stripeRequest.setAmount(amountInVND);
            stripeRequest.setProductName("Add-On - " + contractAddOn.getService().getName());
            stripeRequest.setDescription(contractAddOn.getService().getDescription());
            stripeRequest.setCustomerEmail(payer.getEmail());
            stripeRequest.setQuantity(1);

            StripeCheckoutResponse stripeResponse = stripeService.createCheckoutSession(stripeRequest);

            payment.setGatewayTransactionId(stripeResponse.getSessionId());
            paymentRepository.save(payment);

            return BaseResponse.success(stripeResponse, "Add-on payment session created");

        } catch (StripeException e) {
            log.error("Stripe API error: ", e);
            throw new AppException("Failed to create Stripe payment: " + e.getMessage());
        }
    }


    /**
     * Xử lý callback từ Stripe webhook khi thanh toán thành công
     * Method này được gọi từ StripeController webhook handler
     */
    @Transactional
    public void handleStripePaymentSuccess(String sessionId) {
        try {
            log.info("Processing Stripe payment success for session: {}", sessionId);

            // Tìm payment bằng sessionId
            Payment payment = paymentRepository.findByGatewayTransactionId(sessionId)
                    .orElseThrow(() -> new AppException("Payment not found for session: " + sessionId));

            if ("SUCCESS".equals(payment.getPaymentStatus())) {
                log.info("Payment already marked as SUCCESS: {}", payment.getPaymentId());
                return; // Idempotent - đã xử lý rồi
            }

            // Update payment status
            payment.setPaymentStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Updated payment status to SUCCESS: {}", payment.getPaymentId());

            // Xử lý theo loại payment
            switch (payment.getPaymentType()) {
                case PACKAGE:
                    handleListingPackagePaymentSuccess(payment);
                    break;
                case MEMBERSHIP:
                    handleMembershipPaymentSuccess(payment);
                    break;
                case ADDON:
                    handleAddOnPaymentSuccess(payment);
                    break;
            }

        } catch (Exception e) {
            log.error("Error handling Stripe payment success: ", e);
            throw new AppException("Failed to process payment success: " + e.getMessage());
        }
    }

    private void handleListingPackagePaymentSuccess(Payment payment) {
        ListingPackage listingPackage = listingPackageRepository.findById(payment.getListingPackageId())
                .orElseThrow(() -> new AppException("Listing package not found"));

        listingPackage.setStatus("ACTIVE");
        listingPackageRepository.save(listingPackage);

        log.info("Activated listing package: {}", listingPackage.getListingPackageId());
    }

    private void handleMembershipPaymentSuccess(Payment payment) {
        // TODO: Implement membership activation logic
        log.info("Membership payment successful for user: {}", payment.getPayer().getUserId());
        // Có thể update user membership status, expiry date, etc.
    }

    private void handleAddOnPaymentSuccess(Payment payment) {
        ContractAddOn contractAddOn = contractAddOnRepository.findById(payment.getContractAddOnId())
                .orElseThrow(() -> new AppException("Contract add-on not found"));

        // ContractAddOn doesn't have status field
        // Payment success is recorded in Payment table
        // The existence of successful payment indicates the add-on is activated
        
        log.info("Contract add-on payment successful: {}", contractAddOn.getId());
    }

    /**
     * Xử lý khi payment failed
     */
    @Transactional
    public void handleStripePaymentFailed(String sessionId) {
        try {
            Payment payment = paymentRepository.findByGatewayTransactionId(sessionId)
                    .orElseThrow(() -> new AppException("Payment not found"));

            payment.setPaymentStatus("FAILED");
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info("Updated payment status to FAILED: {}", payment.getPaymentId());

        } catch (Exception e) {
            log.error("Error handling Stripe payment failed: ", e);
        }
    }
}

