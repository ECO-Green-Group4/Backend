package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private ContractRepository contractRepository;
    @Autowired private ListingPackageRepository listingPackageRepository;
    @Autowired private ContractAddOnRepository contractAddOnRepository;

    @Override
    public BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer) {
        try {
            ListingPackage lp = listingPackageRepository.findById(listingPackageId)
                    .orElseThrow(() -> new AppException("Listing package not found"));
            if (!"PENDING_PAYMENT".equals(lp.getStatus()) && !"ACTIVE".equals(lp.getStatus())) {
                throw new AppException("Listing package is not payable");
            }
            BigDecimal amount = lp.getServicePackage() != null ? lp.getServicePackage().getListingFee() : null;
            if (amount == null) throw new AppException("Package fee is not configured");
            Payment payment = new Payment();
            payment.setListingPackage(lp);
            payment.setPayer(payer);
            payment.setPaymentMethod("VNpay");
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus("SUCCESS");
            Payment saved = paymentRepository.save(payment);

            lp.setStatus("ACTIVE");
            listingPackageRepository.save(lp);

            return BaseResponse.success(toResponse(saved), "Listing package paid successfully");
        } catch (Exception e) {
            throw new AppException("Failed to pay listing package: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContract(Long contractId, User payer) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Contract not found"));
            BigDecimal amount = contract.getOrder() != null ? contract.getOrder().getTotalAmount() : null;
            if (amount == null) throw new AppException("Contract amount is not available");
            Payment payment = new Payment();
            payment.setContract(contract);
            payment.setPayer(payer);
            payment.setPaymentMethod("VNpay");
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus("SUCCESS");
            Payment saved = paymentRepository.save(payment);
            return BaseResponse.success(toResponse(saved), "Contract paid successfully");
        } catch (Exception e) {
            throw new AppException("Failed to pay contract: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer) {
        try {
            ContractAddOn addOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));
            BigDecimal amount = addOn.getFee();
            if (amount == null) throw new AppException("Add-on fee is not available");
            Payment payment = new Payment();
            payment.setContract(addOn.getContract());
            payment.setPayer(payer);
            payment.setPaymentMethod("VNpay");
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus("SUCCESS");
            Payment saved = paymentRepository.save(payment);
            return BaseResponse.success(toResponse(saved), "Contract add-on paid successfully");
        } catch (Exception e) {
            throw new AppException("Failed to pay contract add-on: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<PaymentResponse>> getMyPayments(User user) {
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getPayer() != null && p.getPayer().getUserId() == user.getUserId())
                .collect(Collectors.toList());
        return BaseResponse.success(payments.stream().map(this::toResponse).collect(Collectors.toList()), "Payments retrieved");
    }

    @Override
    public BaseResponse<List<PaymentResponse>> getPaymentsByContract(Long contractId, User user) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException("Contract not found"));
        List<Payment> payments = paymentRepository.findByContract(contract);
        return BaseResponse.success(payments.stream().map(this::toResponse).collect(Collectors.toList()), "Payments by contract retrieved");
    }

    @Override
    public BaseResponse<List<PaymentResponse>> getPaymentsByListingPackage(Long listingPackageId, User user) {
        ListingPackage lp = listingPackageRepository.findById(listingPackageId)
                .orElseThrow(() -> new AppException("Listing package not found"));
        List<Payment> payments = paymentRepository.findAll().stream()
                .filter(p -> p.getListingPackage() != null && p.getListingPackage().getListingPackageId().equals(listingPackageId))
                .collect(Collectors.toList());
        return BaseResponse.success(payments.stream().map(this::toResponse).collect(Collectors.toList()), "Payments by package retrieved");
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .contractId(p.getContract() != null ? p.getContract().getContractId() : null)
                .listingPackageId(p.getListingPackage() != null ? p.getListingPackage().getListingPackageId() : null)
                .payerId(p.getPayer() != null ? (long) p.getPayer().getUserId() : null)
                .payerName(p.getPayer() != null ? p.getPayer().getFullName() : null)
                .paymentMethod(p.getPaymentMethod())
                .amount(p.getAmount())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .build();
    }
}
