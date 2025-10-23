package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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