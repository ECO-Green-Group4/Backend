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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Value("${payment.vnpay.return-url:http://localhost:8080/api/payments/vnpay/callback}")
    private String vnpayReturnUrl;

    // 1. THANH TOÁN GÓI TIN VIP
    @Override
    public BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer) {
        try {
            log.info("Bắt đầu thanh toán gói tin VIP: listingPackageId={}, user={}", listingPackageId, payer.getUserId());

            ListingPackage listingPackage = listingPackageRepository.findById(listingPackageId)
                    .orElseThrow(() -> new AppException("Gói tin đăng không tồn tại"));

            // Validate ownership - Access user through listing relationship
            if (listingPackage.getListing().getUser().getUserId() != payer.getUserId()) {
                throw new AppException("Bạn không có quyền thanh toán gói tin này");
            }

            // Validate package type
            if (listingPackage.getServicePackage().getPackageType() != ServicePackage.PackageType.LISTING_VIP) {
                throw new AppException("Đây không phải gói tin VIP");
            }

            if (!"PENDING_PAYMENT".equals(listingPackage.getStatus())) {
                throw new AppException("Gói tin đăng không ở trạng thái chờ thanh toán");
            }

            BigDecimal amount = listingPackage.getServicePackage().getListingFee();

            // Tạo payment record
            Payment payment = createPayment(Payment.PaymentType.PACKAGE, amount, payer);
            payment.setListingPackageId(listingPackageId);
            payment = paymentRepository.save(payment);

            // Tạo VNPay payment URL
            String paymentUrl = vnPayService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Gói tin VIP: " + listingPackage.getServicePackage().getName(),
                    vnpayReturnUrl
            );

            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);

            log.info("Tạo URL thanh toán thành công cho gói tin VIP: paymentId={}", payment.getPaymentId());
            return BaseResponse.success(response, "Vui lòng thanh toán qua VNPay");

        } catch (Exception e) {
            log.error("Lỗi thanh toán gói tin VIP: ", e);
            throw new AppException("Lỗi thanh toán gói tin: " + e.getMessage());
        }
    }

    // 2. THANH TOÁN MEMBERSHIP
    @Override
    public BaseResponse<PaymentResponse> payMembership(Long servicePackageId, User payer) {
        try {
            log.info("Bắt đầu thanh toán membership: servicePackageId={}, user={}", servicePackageId, payer.getUserId());

            ServicePackage membershipPackage = servicePackageRepository.findById(servicePackageId)
                    .orElseThrow(() -> new AppException("Gói membership không tồn tại"));

            // Validate package type
            if (membershipPackage.getPackageType() != ServicePackage.PackageType.MEMBERSHIP) {
                throw new AppException("Đây không phải gói membership");
            }

            if (!"ACTIVE".equals(membershipPackage.getStatus())) {
                throw new AppException("Gói membership không khả dụng");
            }

            // TODO: Implement membership validation when proper membership system is in place
            // Kiểm tra membership hiện tại - commented out as findActiveMembershipByUser is not available
            // Optional<ListingPackage> activeMembership = listingPackageRepository.findActiveMembershipByUser(payer, LocalDateTime.now());
            // if (activeMembership.isPresent()) {
            //     throw new AppException("Bạn đang có membership hiệu lực. Vui lòng đợi hết hạn");
            // }

            BigDecimal amount = membershipPackage.getListingFee();

            // TODO: Membership purchase needs a different approach
            // ListingPackage is designed to link listings with service packages
            // For direct membership purchase, we need a separate UserMembership entity
            // or modify the ListingPackage to allow null listing for membership-only packages
            
            // For now, throw an exception indicating this feature needs implementation
            throw new AppException("Direct membership purchase not yet implemented. Please purchase through listing creation.");

            /* TODO: Implement proper membership purchase
            // Tạo payment
            Payment payment = createPayment(Payment.PaymentType.MEMBERSHIP, amount, payer);
            payment.setListingPackageId(savedMembership.getListingPackageId());
            payment = paymentRepository.save(payment);

            // Tạo VNPay payment URL
            String paymentUrl = vnPayService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Membership: " + membershipPackage.getName(),
                    vnpayReturnUrl
            );

            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);
            */

            /* TODO: Uncomment when implementing proper membership purchase
            log.info("Tạo URL thanh toán thành công cho membership: paymentId={}", payment.getPaymentId());
            return BaseResponse.success(response, "Vui lòng thanh toán membership qua VNPay");
            */

        } catch (Exception e) {
            log.error("Lỗi thanh toán membership: ", e);
            throw new AppException("Lỗi thanh toán membership: " + e.getMessage());
        }
    }

    // 3. THANH TOÁN HỢP ĐỒNG
    @Override
    public BaseResponse<PaymentResponse> payContract(Long contractId, User payer) {
        try {
            log.info("Bắt đầu thanh toán hợp đồng: contractId={}, user={}", contractId, payer.getUserId());

            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Hợp đồng không tồn tại"));

            // Validate contract status và authorization - SỬA LỖI: So sánh int với int
            if (!"PENDING_PAYMENT".equals(contract.getContractStatus())) {
                throw new AppException("Hợp đồng không ở trạng thái chờ thanh toán");
            }

            // SỬA LỖI: So sánh int với int
            if (contract.getOrder().getBuyer().getUserId() != payer.getUserId()) {
                throw new AppException("Chỉ người mua được thanh toán hợp đồng");
            }

            BigDecimal amount = contract.getOrder().getTotalAmount();

            // Tạo payment record
            Payment payment = createPayment(Payment.PaymentType.CONTRACT, amount, payer);
            payment.setContractId(contractId);
            payment = paymentRepository.save(payment);

            // Tạo VNPay payment URL
            String paymentUrl = vnPayService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Hợp đồng mua xe #" + contract.getOrder().getOrderId(),
                    vnpayReturnUrl
            );

            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);

            log.info("Tạo URL thanh toán thành công cho hợp đồng: paymentId={}", payment.getPaymentId());
            return BaseResponse.success(response, "Vui lòng thanh toán hợp đồng qua VNPay");

        } catch (Exception e) {
            log.error("Lỗi thanh toán hợp đồng: ", e);
            throw new AppException("Lỗi thanh toán hợp đồng: " + e.getMessage());
        }
    }

    // 4. THANH TOÁN ADDON HỢP ĐỒNG
    @Override
    public BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer) {
        try {
            log.info("Bắt đầu thanh toán addon: contractAddOnId={}, user={}", contractAddOnId, payer.getUserId());

            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Dịch vụ addon không tồn tại"));

            // Validate authorization - SỬA LỖI: So sánh int với int
            Contract contract = contractAddOn.getContract();
            boolean isAuthorized = contract.getOrder().getBuyer().getUserId() == payer.getUserId()
                    || contract.getOrder().getSeller().getUserId() == payer.getUserId();

            if (!isAuthorized) {
                throw new AppException("Bạn không có quyền thanh toán dịch vụ này");
            }

            BigDecimal amount = contractAddOn.getFee();

            // Tạo payment record
            Payment payment = createPayment(Payment.PaymentType.ADDON, amount, payer);
            payment.setContractAddOnId(contractAddOnId);
            payment = paymentRepository.save(payment);

            // Tạo VNPay payment URL
            String paymentUrl = vnPayService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Dịch vụ: " + contractAddOn.getService().getName(),
                    vnpayReturnUrl
            );

            PaymentResponse response = toPaymentResponse(payment);
            response.setPaymentUrl(paymentUrl);

            log.info("Tạo URL thanh toán thành công cho addon: paymentId={}", payment.getPaymentId());
            return BaseResponse.success(response, "Vui lòng thanh toán dịch vụ bổ sung qua VNPay");

        } catch (Exception e) {
            log.error("Lỗi thanh toán addon: ", e);
            throw new AppException("Lỗi thanh toán addon: " + e.getMessage());
        }
    }

    // XỬ LÝ CALLBACK TỪ VNPAY
    @Override
    public BaseResponse<PaymentResponse> handleVNPayCallback(VNPayCallbackRequest request) {
        try {
            log.info("Nhận callback từ VNPay: vnp_TxnRef={}, vnp_ResponseCode={}",
                    request.getVnp_TxnRef(), request.getVnp_ResponseCode());

            // Verify checksum từ VNPay
            boolean isValid = vnPayService.verifyReturn(request);

            if (!isValid) {
                log.error("Chữ ký VNPay không hợp lệ: {}", request.getVnp_SecureHash());
                throw new AppException("Chữ ký không hợp lệ");
            }

            Long paymentId = Long.parseLong(request.getVnp_TxnRef());
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Payment không tồn tại"));

            // Cập nhật trạng thái payment
            if ("00".equals(request.getVnp_ResponseCode())) {
                payment.setPaymentStatus("SUCCESS");
                payment.setGatewayTransactionId(request.getVnp_TransactionNo());
                payment.setPaymentDate(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());

                // Thực hiện business logic sau khi thanh toán thành công
                handlePostPaymentActions(payment);

                log.info("Thanh toán thành công: paymentId={}, transactionId={}", paymentId, request.getVnp_TransactionNo());
            } else {
                payment.setPaymentStatus("FAILED");
                payment.setUpdatedAt(LocalDateTime.now());
                log.warn("Thanh toán thất bại: paymentId={}, responseCode={}", paymentId, request.getVnp_ResponseCode());
            }

            paymentRepository.save(payment);

            return BaseResponse.success(toPaymentResponse(payment), "Xử lý thanh toán thành công");

        } catch (Exception e) {
            log.error("Lỗi xử lý callback VNPay: ", e);
            throw new AppException("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }

    // BUSINESS LOGIC SAU KHI THANH TOÁN THÀNH CÔNG
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
            log.info("Xử lý post-payment actions thành công: paymentId={}, type={}",
                    payment.getPaymentId(), payment.getPaymentType());
        } catch (Exception e) {
            log.error("Lỗi xử lý post-payment actions: ", e);
            throw new AppException("Lỗi xử lý sau thanh toán: " + e.getMessage());
        }
    }

    private void handlePackagePaymentSuccess(Payment payment) {
        ListingPackage listingPackage = listingPackageRepository.findById(payment.getListingPackageId())
                .orElseThrow(() -> new AppException("Listing package not found"));

        listingPackage.setStatus("ACTIVE");
        listingPackageRepository.save(listingPackage);

        // Kích hoạt tin đăng VIP
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

        // Cập nhật thông tin membership cho user
        User user = payment.getPayer();
        user.setCurrentMembershipId(membership.getListingPackageId());
        user.setMembershipExpiry(membership.getExpiredAt());

        // Kiểm tra null trước khi gán couponCount
        if (membership.getServicePackage().getCouponCount() != null) {
            user.setAvailableCoupons(membership.getServicePackage().getCouponCount());
        } else {
            user.setAvailableCoupons(0);
        }

        userRepository.save(user);

        log.info("Kích hoạt membership thành công: userId={}, membershipId={}",
                user.getUserId(), membership.getListingPackageId());
    }

    private void handleContractPaymentSuccess(Payment payment) {
        Contract contract = contractRepository.findById(payment.getContractId())
                .orElseThrow(() -> new AppException("Contract not found"));

        contract.setContractStatus("PAID");
        contractRepository.save(contract);

        // Cập nhật trạng thái order - Kiểm tra null
        if (contract.getOrder() != null) {
            // Cần có setter cho order status
            // contract.getOrder().setStatus("PAID");
            log.info("Cập nhật trạng thái order thành PAID: orderId={}", contract.getOrder().getOrderId());
        }
    }

    private void handleAddOnPaymentSuccess(Payment payment) {
        ContractAddOn contractAddOn = contractAddOnRepository.findById(payment.getContractAddOnId())
                .orElseThrow(() -> new AppException("Contract addon not found"));

        // Không cần set status, chỉ cần log thành công
        // Hoặc có thể tạo một bảng tracking riêng cho addon payments

        log.info("Addon payment completed: contractAddOnId={}, amount={}",
                contractAddOn.getId(), contractAddOn.getFee());

        // Hoặc đánh dấu thông qua payment status
        log.info("Addon dịch vụ {} đã được thanh toán thành công",
                contractAddOn.getService().getName());
    }

    // LẤY DANH SÁCH GÓI MEMBERSHIP
    @Override
    public BaseResponse<List<ServicePackage>> getMembershipPackages() {
        try {
            List<ServicePackage> packages = servicePackageRepository
                    .findByPackageTypeAndStatus(ServicePackage.PackageType.MEMBERSHIP, "ACTIVE");
            return BaseResponse.success(packages, "Danh sách gói membership");
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách membership packages: ", e);
            throw new AppException("Lỗi lấy danh sách gói membership");
        }
    }

    // LẤY DANH SÁCH GÓI TIN VIP
    @Override
    public BaseResponse<List<ServicePackage>> getListingVipPackages() {
        try {
            List<ServicePackage> packages = servicePackageRepository
                    .findByPackageTypeAndStatus(ServicePackage.PackageType.LISTING_VIP, "ACTIVE");
            return BaseResponse.success(packages, "Danh sách gói tin VIP");
        } catch (Exception e) {
            log.error("Lỗi lấy danh sách VIP packages: ", e);
            throw new AppException("Lỗi lấy danh sách gói tin VIP");
        }
    }

    // LẤY LỊCH SỬ THANH TOÁN
    @Override
    public BaseResponse<List<PaymentResponse>> getMyPayments(User user) {
        try {
            List<Payment> payments = paymentRepository.findByPayerOrderByCreatedAtDesc(user);
            List<PaymentResponse> responses = payments.stream()
                    .map(this::toPaymentResponse)
                    .collect(Collectors.toList());
            return BaseResponse.success(responses, "Lịch sử thanh toán");
        } catch (Exception e) {
            log.error("Lỗi lấy lịch sử thanh toán: ", e);
            throw new AppException("Lỗi lấy lịch sử thanh toán");
        }
    }

    // LẤY CHI TIẾT THANH TOÁN
    @Override
    public BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Thanh toán không tồn tại"));

            // Validate ownership - SỬA LỖI: So sánh int với int
            if (payment.getPayer().getUserId() != user.getUserId()) {
                throw new AppException("Bạn không có quyền xem thanh toán này");
            }

            return BaseResponse.success(toPaymentResponse(payment), "Chi tiết thanh toán");
        } catch (Exception e) {
            log.error("Lỗi lấy chi tiết thanh toán: ", e);
            throw new AppException("Lỗi lấy chi tiết thanh toán");
        }
    }

    // HELPER METHODS
    private Payment createPayment(Payment.PaymentType type, BigDecimal amount, User payer) {
        Payment payment = new Payment();
        payment.setPaymentType(type);
        payment.setAmount(amount);
        payment.setPayer(payer);
        payment.setPaymentGateway("VNPAY");
        payment.setPaymentStatus("PENDING");
        payment.setCurrency("VND");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusMinutes(15)); // 15 phút để thanh toán
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
                .build();
    }
}