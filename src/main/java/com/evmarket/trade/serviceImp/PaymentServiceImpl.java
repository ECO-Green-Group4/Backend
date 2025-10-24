package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.VNPayCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.PaymentService;
import com.evmarket.trade.service.VNPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ListingPackageRepository listingPackageRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractAddOnRepository contractAddOnRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private VNPayService vnPayService;

    @Override
    public BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer) {
        try {
            log.info("Starting payment for listing package: listingPackageId={}, user={}", listingPackageId, payer.getUserId());

            ListingPackage listingPackage = listingPackageRepository.findById(listingPackageId)
                    .orElseThrow(() -> new AppException("Listing package not found"));

            if (listingPackage.getListing().getUser().getUserId() != payer.getUserId()) {
                throw new AppException("You are not authorized to pay for this package");
            }

            if (!"PENDING_PAYMENT".equals(listingPackage.getStatus())) {
                throw new AppException("Listing package is not in pending payment status");
            }

            BigDecimal amount = listingPackage.getServicePackage().getListingFee();

            // Create payment record
            Payment payment = createPayment(Payment.PaymentType.PACKAGE, amount, payer);
            payment.setListingPackageId(listingPackageId);
            payment = paymentRepository.save(payment);

            PaymentResponse response = toPaymentResponse(payment);

            return BaseResponse.success(response, "Payment created. Use Stripe endpoints for payment processing.");

        } catch (Exception e) {
            log.error("Error processing listing package payment: ", e);
            throw new AppException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payMembership(Long servicePackageId, User payer) {
        try {
            log.info("Starting membership payment: servicePackageId={}, user={}", servicePackageId, payer.getUserId());

            ServicePackage servicePackage = servicePackageRepository.findById(servicePackageId)
                    .orElseThrow(() -> new AppException("Service package not found"));

            if (servicePackage.getPackageType() != ServicePackage.PackageType.MEMBERSHIP) {
                throw new AppException("This is not a membership package");
            }

            BigDecimal amount = servicePackage.getListingFee();

            // Create payment record
            Payment payment = createPayment(Payment.PaymentType.MEMBERSHIP, amount, payer);
            payment = paymentRepository.save(payment);

            PaymentResponse response = toPaymentResponse(payment);

            return BaseResponse.success(response, "Payment created. Use Stripe endpoints for payment processing.");

        } catch (Exception e) {
            log.error("Error processing membership payment: ", e);
            throw new AppException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContract(Long contractId, User payer) {
        try {
            log.info("Starting contract payment: contractId={}, user={}", contractId, payer.getUserId());

            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Contract not found"));

            if (contract.getOrder().getBuyer().getUserId() != payer.getUserId()) {
                throw new AppException("You are not authorized to pay for this contract");
            }

            if (!"PENDING_PAYMENT".equals(contract.getContractStatus())) {
                throw new AppException("Contract is not in pending payment status");
            }

            BigDecimal amount = contract.getOrder().getTotalAmount();

            // Create payment record
            Payment payment = createPayment(Payment.PaymentType.CONTRACT, amount, payer);
            payment.setContractId(contractId);
            payment = paymentRepository.save(payment);

            PaymentResponse response = toPaymentResponse(payment);

            return BaseResponse.success(response, "Payment created. Use Stripe endpoints for payment processing.");

        } catch (Exception e) {
            log.error("Error processing contract payment: ", e);
            throw new AppException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer) {
        try {
            log.info("Starting contract add-on payment: contractAddOnId={}, user={}", contractAddOnId, payer.getUserId());

            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));

            if (contractAddOn.getContract().getOrder().getBuyer().getUserId() != payer.getUserId()) {
                throw new AppException("You are not authorized to pay for this add-on");
            }

            BigDecimal amount = contractAddOn.getFee();

            // Create payment record
            Payment payment = createPayment(Payment.PaymentType.ADDON, amount, payer);
            payment.setContractAddOnId(contractAddOnId);
            payment = paymentRepository.save(payment);

            PaymentResponse response = toPaymentResponse(payment);

            return BaseResponse.success(response, "Payment created. Use Stripe endpoints for payment processing.");

        } catch (Exception e) {
            log.error("Error processing contract add-on payment: ", e);
            throw new AppException("Error processing payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<ServicePackage>> getMembershipPackages() {
        try {
            List<ServicePackage> packages = servicePackageRepository.findByPackageTypeAndStatus(
                    ServicePackage.PackageType.MEMBERSHIP, "ACTIVE");
            return BaseResponse.success(packages, "Membership packages retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving membership packages: ", e);
            throw new AppException("Error retrieving membership packages: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<ServicePackage>> getListingVipPackages() {
        try {
            List<ServicePackage> packages = servicePackageRepository.findByPackageTypeAndStatus(
                    ServicePackage.PackageType.LISTING_VIP, "ACTIVE");
            return BaseResponse.success(packages, "VIP packages retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving VIP packages: ", e);
            throw new AppException("Error retrieving VIP packages: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<PaymentResponse>> getMyPayments(User user) {
        try {
            List<Payment> payments = paymentRepository.findByPayerOrderByCreatedAtDesc(user);
            List<PaymentResponse> responses = payments.stream()
                    .map(this::toPaymentResponse)
                    .collect(Collectors.toList());
            return BaseResponse.success(responses, "Payment history retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving payment history: ", e);
            throw new AppException("Error retrieving payment history: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Payment not found"));

            if (payment.getPayer().getUserId() != user.getUserId()) {
                throw new AppException("You are not authorized to view this payment");
            }

            PaymentResponse response = toPaymentResponse(payment);
            return BaseResponse.success(response, "Payment details retrieved successfully");
        } catch (Exception e) {
            log.error("Error retrieving payment details: ", e);
            throw new AppException("Error retrieving payment details: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payListingPackageWithVNPay(Long listingPackageId, User payer, String ipAddress) {
        try {
            log.info("Starting VIP package payment with VNPay: listingPackageId={}, user={}", listingPackageId, payer.getUserId());

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
                throw new AppException("This package has already been paid successfully");
            }

            BigDecimal amount = listingPackage.getServicePackage().getListingFee();

            // Check if there is a pending payment
            Payment payment;
            List<Payment> pendingPayments = paymentRepository.findByListingPackageIdAndPaymentStatus(
                    listingPackageId, "PENDING");
            
            if (!pendingPayments.isEmpty()) {
                payment = pendingPayments.get(0);
                
                if (payment.getExpiryTime() != null && payment.getExpiryTime().isBefore(LocalDateTime.now())) {
                    payment.setPaymentStatus("EXPIRED");
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    
                    payment = createVNPayPayment(Payment.PaymentType.PACKAGE, amount, payer);
                    payment.setListingPackageId(listingPackageId);
                    payment = paymentRepository.save(payment);
                    
                    log.info("Old payment expired, created new payment: paymentId={}", payment.getPaymentId());
                } else {
                    log.info("Reusing existing pending payment: paymentId={}", payment.getPaymentId());
                }
            } else {
                payment = createVNPayPayment(Payment.PaymentType.PACKAGE, amount, payer);
                payment.setListingPackageId(listingPackageId);
                payment = paymentRepository.save(payment);
                
                log.info("Created new payment: paymentId={}", payment.getPaymentId());
            }

            // Call VNPay service to create payment
            String txnRef = payment.getPaymentId() + "_" + System.currentTimeMillis();
            String paymentUrl = vnPayService.createPaymentUrl(
                    amount,
                    "EV Trade - VIP Package: " + listingPackage.getServicePackage().getName(),
                    txnRef,
                    ipAddress,
                    null
            );

            // Create response
            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);
            
            Map<String, Object> vnpayData = new HashMap<>();
            vnpayData.put("paymentUrl", paymentUrl);
            vnpayData.put("txnRef", txnRef);
            response.setGatewayResponse(vnpayData);

            log.info("VNPay payment created successfully. Payment URL: {}", paymentUrl);

            return BaseResponse.success(response, "VNPay payment created successfully");

        } catch (Exception e) {
            log.error("Error creating VIP package payment with VNPay: ", e);
            throw new AppException("Error creating package payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContractWithVNPay(Long contractId, User payer, String ipAddress) {
        try {
            log.info("Starting contract payment with VNPay: contractId={}, user={}", contractId, payer.getUserId());

            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Contract not found"));

            if (!"PENDING_PAYMENT".equals(contract.getContractStatus())) {
                throw new AppException("Contract is not in pending payment status");
            }

            if (contract.getOrder().getBuyer().getUserId() != payer.getUserId()) {
                throw new AppException("Only the buyer can pay for the contract");
            }

            // Check if payment already succeeded
            List<Payment> successPayments = paymentRepository.findByContractIdAndPaymentStatus(
                    contractId, "SUCCESS");
            if (!successPayments.isEmpty()) {
                throw new AppException("This contract has already been paid successfully");
            }

            BigDecimal amount = contract.getOrder().getTotalAmount();

            // Check if there is a pending payment
            Payment payment;
            List<Payment> pendingPayments = paymentRepository.findByContractIdAndPaymentStatus(
                    contractId, "PENDING");
            
            if (!pendingPayments.isEmpty()) {
                payment = pendingPayments.get(0);
                
                if (payment.getExpiryTime() != null && payment.getExpiryTime().isBefore(LocalDateTime.now())) {
                    payment.setPaymentStatus("EXPIRED");
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    
                    payment = createVNPayPayment(Payment.PaymentType.CONTRACT, amount, payer);
                    payment.setContractId(contractId);
                    payment = paymentRepository.save(payment);
                    
                    log.info("Old payment expired, created new payment: paymentId={}", payment.getPaymentId());
                } else {
                    log.info("Reusing existing pending payment: paymentId={}", payment.getPaymentId());
                }
            } else {
                payment = createVNPayPayment(Payment.PaymentType.CONTRACT, amount, payer);
                payment.setContractId(contractId);
                payment = paymentRepository.save(payment);
                
                log.info("Created new payment: paymentId={}", payment.getPaymentId());
            }

            // Call VNPay service to create payment
            String txnRef = payment.getPaymentId() + "_" + System.currentTimeMillis();
            String paymentUrl = vnPayService.createPaymentUrl(
                    amount,
                    "EV Trade - Vehicle Purchase Contract #" + contract.getOrder().getOrderId(),
                    txnRef,
                    ipAddress,
                    null
            );

            // Create response
            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);
            
            Map<String, Object> vnpayData = new HashMap<>();
            vnpayData.put("paymentUrl", paymentUrl);
            vnpayData.put("txnRef", txnRef);
            response.setGatewayResponse(vnpayData);

            log.info("VNPay payment created successfully. Payment URL: {}", paymentUrl);

            return BaseResponse.success(response, "VNPay payment created successfully");

        } catch (Exception e) {
            log.error("Error creating contract payment with VNPay: ", e);
            throw new AppException("Error creating contract payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContractAddOnWithVNPay(Long contractAddOnId, User payer, String ipAddress) {
        try {
            log.info("Starting addon payment with VNPay: contractAddOnId={}, user={}", contractAddOnId, payer.getUserId());

            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Addon service not found"));

            Contract contract = contractAddOn.getContract();
            boolean isAuthorized = contract.getOrder().getBuyer().getUserId() == payer.getUserId()
                    || contract.getOrder().getSeller().getUserId() == payer.getUserId();

            if (!isAuthorized) {
                throw new AppException("You are not authorized to pay for this service");
            }

            // Check if payment already succeeded
            List<Payment> successPayments = paymentRepository.findByContractAddOnIdAndPaymentStatus(
                    contractAddOnId, "SUCCESS");
            if (!successPayments.isEmpty()) {
                throw new AppException("This addon service has already been paid successfully");
            }

            BigDecimal amount = contractAddOn.getFee();

            // Check if there is a pending payment
            Payment payment;
            List<Payment> pendingPayments = paymentRepository.findByContractAddOnIdAndPaymentStatus(
                    contractAddOnId, "PENDING");
            
            if (!pendingPayments.isEmpty()) {
                payment = pendingPayments.get(0);
                
                if (payment.getExpiryTime() != null && payment.getExpiryTime().isBefore(LocalDateTime.now())) {
                    payment.setPaymentStatus("EXPIRED");
                    payment.setUpdatedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    
                    payment = createVNPayPayment(Payment.PaymentType.ADDON, amount, payer);
                    payment.setContractAddOnId(contractAddOnId);
                    payment = paymentRepository.save(payment);
                    
                    log.info("Old payment expired, created new payment: paymentId={}", payment.getPaymentId());
                } else {
                    log.info("Reusing existing pending payment: paymentId={}", payment.getPaymentId());
                }
            } else {
                payment = createVNPayPayment(Payment.PaymentType.ADDON, amount, payer);
                payment.setContractAddOnId(contractAddOnId);
                payment = paymentRepository.save(payment);
                
                log.info("Created new payment: paymentId={}", payment.getPaymentId());
            }

            // Call VNPay service to create payment
            String txnRef = payment.getPaymentId() + "_" + System.currentTimeMillis();
            String paymentUrl = vnPayService.createPaymentUrl(
                    amount,
                    "EV Trade - Service: " + contractAddOn.getService().getName(),
                    txnRef,
                    ipAddress,
                    null
            );

            // Create response
            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);
            
            Map<String, Object> vnpayData = new HashMap<>();
            vnpayData.put("paymentUrl", paymentUrl);
            vnpayData.put("txnRef", txnRef);
            response.setGatewayResponse(vnpayData);

            log.info("VNPay payment created successfully. Payment URL: {}", paymentUrl);

            return BaseResponse.success(response, "VNPay payment created successfully");

        } catch (Exception e) {
            log.error("Error creating addon payment with VNPay: ", e);
            throw new AppException("Error creating addon payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> handleVNPayCallback(VNPayCallbackRequest request) {
        try {
            log.info("Received VNPay callback: txnRef={}, responseCode={}, transactionNo={}",
                    request.getVnp_TxnRef(), request.getVnp_ResponseCode(), request.getVnp_TransactionNo());

            // Convert request to Map for verification
            Map<String, String> params = new HashMap<>();
            if (request.getVnp_TmnCode() != null) params.put("vnp_TmnCode", request.getVnp_TmnCode());
            if (request.getVnp_Amount() != null) params.put("vnp_Amount", String.valueOf(request.getVnp_Amount()));
            if (request.getVnp_BankCode() != null) params.put("vnp_BankCode", request.getVnp_BankCode());
            if (request.getVnp_BankTranNo() != null) params.put("vnp_BankTranNo", request.getVnp_BankTranNo());
            if (request.getVnp_CardType() != null) params.put("vnp_CardType", request.getVnp_CardType());
            if (request.getVnp_OrderInfo() != null) params.put("vnp_OrderInfo", request.getVnp_OrderInfo());
            if (request.getVnp_PayDate() != null) params.put("vnp_PayDate", request.getVnp_PayDate());
            if (request.getVnp_ResponseCode() != null) params.put("vnp_ResponseCode", request.getVnp_ResponseCode());
            if (request.getVnp_TransactionNo() != null) params.put("vnp_TransactionNo", request.getVnp_TransactionNo());
            if (request.getVnp_TxnRef() != null) params.put("vnp_TxnRef", request.getVnp_TxnRef());
            if (request.getVnp_TransactionStatus() != null) params.put("vnp_TransactionStatus", request.getVnp_TransactionStatus());
            if (request.getVnp_SecureHash() != null) params.put("vnp_SecureHash", request.getVnp_SecureHash());

            // Verify callback signature
            boolean isValid = vnPayService.verifyCallback(params);

            if (!isValid) {
                log.error("Invalid VNPay signature: {}", request.getVnp_SecureHash());
                throw new AppException("Invalid signature");
            }

            // Parse paymentId from txnRef (format: "paymentId_timestamp")
            String txnRef = request.getVnp_TxnRef();
            Long paymentId;
            if (txnRef.contains("_")) {
                paymentId = Long.parseLong(txnRef.substring(0, txnRef.indexOf("_")));
            } else {
                paymentId = Long.parseLong(txnRef);
            }
            
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Payment not found"));

            if (request.isSuccess()) {
                payment.setPaymentStatus("SUCCESS");
                payment.setGatewayTransactionId(request.getVnp_TransactionNo());
                payment.setPaymentDate(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());

                handlePostPaymentActions(payment);

                log.info("VNPay payment successful: paymentId={}, transactionNo={}",
                        paymentId, request.getVnp_TransactionNo());
            } else {
                payment.setPaymentStatus("FAILED");
                payment.setUpdatedAt(LocalDateTime.now());
                log.warn("VNPay payment failed: paymentId={}, responseCode={}, message={}",
                        paymentId, request.getVnp_ResponseCode(), request.getResponseMessage());
            }

            paymentRepository.save(payment);

            return BaseResponse.success(toPaymentResponse(payment), 
                    request.isSuccess() ? "VNPay payment successful" : request.getResponseMessage());

        } catch (Exception e) {
            log.error("Error processing VNPay callback: ", e);
            throw new AppException("Error processing VNPay payment: " + e.getMessage());
        }
    }

    private void handlePostPaymentActions(Payment payment) {
        try {
            switch (payment.getPaymentType()) {
                case PACKAGE:
                    handlePackagePaymentSuccess(payment);
                    break;
                case MEMBERSHIP:
                    handleMembershipPaymentSuccess(payment);
                    break;
                case CONTRACT:
                    handleContractPaymentSuccess(payment);
                    break;
                case ADDON:
                    handleAddOnPaymentSuccess(payment);
                    break;
                default:
                    log.warn("Unknown payment type: {}", payment.getPaymentType());
            }
            log.info("Post-payment actions completed successfully: paymentId={}, type={}",
                    payment.getPaymentId(), payment.getPaymentType());
        } catch (Exception e) {
            log.error("Error handling post-payment actions: ", e);
            throw new AppException("Error handling post-payment actions: " + e.getMessage());
        }
    }

    private void handlePackagePaymentSuccess(Payment payment) {
        ListingPackage listingPackage = listingPackageRepository.findById(payment.getListingPackageId())
                .orElseThrow(() -> new AppException("Listing package not found"));

        listingPackage.setStatus("ACTIVE");
        listingPackageRepository.save(listingPackage);

        Listing listing = listingPackage.getListing();
        if (listing != null) {
            listing.setStatus("ACTIVE");
            listingRepository.save(listing);
        }
    }

    private void handleMembershipPaymentSuccess(Payment payment) {
        ListingPackage membership = listingPackageRepository.findById(payment.getListingPackageId())
                .orElseThrow(() -> new AppException("Membership not found"));

        membership.setStatus("ACTIVE");
        listingPackageRepository.save(membership);

        User user = payment.getPayer();
        user.setCurrentMembershipId(membership.getListingPackageId());
        user.setMembershipExpiry(membership.getExpiredAt());

        if (membership.getServicePackage().getCouponCount() != null) {
            user.setAvailableCoupons(membership.getServicePackage().getCouponCount());
        } else {
            user.setAvailableCoupons(0);
        }

        userRepository.save(user);

        log.info("Membership activated successfully: userId={}, membershipId={}",
                user.getUserId(), membership.getListingPackageId());
    }

    private void handleContractPaymentSuccess(Payment payment) {
        Contract contract = contractRepository.findById(payment.getContractId())
                .orElseThrow(() -> new AppException("Contract not found"));

        contract.setContractStatus("PAID");
        contractRepository.save(contract);

        if (contract.getOrder() != null) {
            log.info("Order status updated to PAID: orderId={}", contract.getOrder().getOrderId());
        }
    }

    private void handleAddOnPaymentSuccess(Payment payment) {
        ContractAddOn contractAddOn = contractAddOnRepository.findById(payment.getContractAddOnId())
                .orElseThrow(() -> new AppException("Contract addon not found"));

        log.info("Addon payment completed: contractAddOnId={}, amount={}",
                contractAddOn.getId(), contractAddOn.getFee());

        log.info("Addon service {} has been paid successfully",
                contractAddOn.getService().getName());
    }

    // Helper methods
    private Payment createPayment(Payment.PaymentType paymentType, BigDecimal amount, User payer) {
        Payment payment = new Payment();
        payment.setPaymentType(paymentType);
        payment.setPayer(payer);
        payment.setAmount(amount);
        payment.setCurrency("VND");
        payment.setPaymentStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusMinutes(30));
        return payment;
    }

    private Payment createVNPayPayment(Payment.PaymentType paymentType, BigDecimal amount, User payer) {
        Payment payment = new Payment();
        payment.setPaymentType(paymentType);
        payment.setPayer(payer);
        payment.setAmount(amount);
        payment.setCurrency("VND");
        payment.setPaymentGateway("VNPAY");
        payment.setPaymentStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusMinutes(15)); // VNPay payment expires after 15 minutes
        return payment;
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentType(payment.getPaymentType().toString())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .expiryTime(payment.getExpiryTime())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .contractId(payment.getContractId())
                .contractAddOnId(payment.getContractAddOnId())
                .listingPackageId(payment.getListingPackageId())
                .build();
    }
}