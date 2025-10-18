package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.MomoCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.MomoService;
import com.evmarket.trade.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    // Các autowired repositories giữ nguyên...
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
    private MomoService moMoService;

    @Value("${payment.momo.return-url:http://localhost:8080/api/payments/momo-callback}")
    private String momoReturnUrl;

    @Override
    public BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer) {
        try {
            log.info("Bắt đầu thanh toán gói tin VIP: listingPackageId={}, user={}", listingPackageId, payer.getUserId());

            ListingPackage listingPackage = listingPackageRepository.findById(listingPackageId)
                    .orElseThrow(() -> new AppException("Gói tin đăng không tồn tại"));

            if (listingPackage.getListing().getUser().getUserId() != payer.getUserId()) {
                throw new AppException("Bạn không có quyền thanh toán gói tin này");
            }

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

            // Gọi MoMo service để tạo thanh toán
            Map<String, Object> momoResponse = moMoService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Gói tin VIP: " + listingPackage.getServicePackage().getName(),
                    momoReturnUrl
            );

            // Tạo response và map data từ MoMo
            PaymentResponse response = toPaymentResponse(payment);

            // Map các field từ MoMo Payment Gateway response
            // payUrl - Link chính để user truy cập và quét QR code
            response.setPaymentUrl((String) momoResponse.get("payUrl"));
            response.setDeeplink((String) momoResponse.get("deeplink"));
            response.setQrCodeUrl((String) momoResponse.get("qrCodeUrl"));
            response.setGatewayResponse(momoResponse);

            log.info("Tạo thanh toán MoMo thành công. Payment URL: {}", response.getPaymentUrl());

            return BaseResponse.success(response, "Tạo thanh toán MoMo thành công. Truy cập paymentUrl để xem QR code và thanh toán.");

        } catch (Exception e) {
            log.error("Lỗi thanh toán gói tin VIP với MoMo: ", e);
            throw new AppException("Lỗi thanh toán gói tin: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContract(Long contractId, User payer) {
        try {
            log.info("Bắt đầu thanh toán hợp đồng: contractId={}, user={}", contractId, payer.getUserId());

            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Hợp đồng không tồn tại"));

            if (!"PENDING_PAYMENT".equals(contract.getContractStatus())) {
                throw new AppException("Hợp đồng không ở trạng thái chờ thanh toán");
            }

            if (contract.getOrder().getBuyer().getUserId() != payer.getUserId()) {
                throw new AppException("Chỉ người mua được thanh toán hợp đồng");
            }

            BigDecimal amount = contract.getOrder().getTotalAmount();

            // Tạo payment record
            Payment payment = createPayment(Payment.PaymentType.CONTRACT, amount, payer);
            payment.setContractId(contractId);
            payment = paymentRepository.save(payment);

            // Gọi MoMo service để tạo thanh toán
            Map<String, Object> momoResponse = moMoService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Hợp đồng mua xe #" + contract.getOrder().getOrderId(),
                    momoReturnUrl
            );

            // Tạo response và map data từ MoMo
            PaymentResponse response = toPaymentResponse(payment);

            // Map các field từ MoMo Payment Gateway response
            // payUrl - Link chính để user truy cập và quét QR code
            response.setPaymentUrl((String) momoResponse.get("payUrl"));
            response.setDeeplink((String) momoResponse.get("deeplink"));
            response.setQrCodeUrl((String) momoResponse.get("qrCodeUrl"));
            response.setGatewayResponse(momoResponse);

            log.info("Tạo thanh toán MoMo thành công. Payment URL: {}", response.getPaymentUrl());

            return BaseResponse.success(response, "Tạo thanh toán MoMo thành công. Truy cập paymentUrl để xem QR code và thanh toán.");

        } catch (Exception e) {
            log.error("Lỗi thanh toán hợp đồng với MoMo: ", e);
            throw new AppException("Lỗi thanh toán hợp đồng: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer) {
        try {
            log.info("Bắt đầu thanh toán addon: contractAddOnId={}, user={}", contractAddOnId, payer.getUserId());

            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Dịch vụ addon không tồn tại"));

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

            // Gọi MoMo service để tạo thanh toán
            Map<String, Object> momoResponse = moMoService.createPayment(
                    amount,
                    payment.getPaymentId().toString(),
                    "EV Trade - Dịch vụ: " + contractAddOn.getService().getName(),
                    momoReturnUrl
            );

            // Tạo response và map data từ MoMo
            PaymentResponse response = toPaymentResponse(payment);

            // Map các field từ MoMo Payment Gateway response
            // payUrl - Link chính để user truy cập và quét QR code
            response.setPaymentUrl((String) momoResponse.get("payUrl"));
            response.setDeeplink((String) momoResponse.get("deeplink"));
            response.setQrCodeUrl((String) momoResponse.get("qrCodeUrl"));
            response.setGatewayResponse(momoResponse);

            log.info("Tạo thanh toán MoMo thành công. Payment URL: {}", response.getPaymentUrl());

            return BaseResponse.success(response, "Tạo thanh toán MoMo thành công. Truy cập paymentUrl để xem QR code và thanh toán.");

        } catch (Exception e) {
            log.error("Lỗi thanh toán addon với MoMo: ", e);
            throw new AppException("Lỗi thanh toán addon: " + e.getMessage());
        }
    }

    // Các method khác giữ nguyên...
    @Override
    public BaseResponse<PaymentResponse> payMembership(Long servicePackageId, User payer) {
        try {
            log.info("Bắt đầu thanh toán membership: servicePackageId={}, user={}", servicePackageId, payer.getUserId());

            ServicePackage membershipPackage = servicePackageRepository.findById(servicePackageId)
                    .orElseThrow(() -> new AppException("Gói membership không tồn tại"));

            if (membershipPackage.getPackageType() != ServicePackage.PackageType.MEMBERSHIP) {
                throw new AppException("Đây không phải gói membership");
            }

            if (!"ACTIVE".equals(membershipPackage.getStatus())) {
                throw new AppException("Gói membership không khả dụng");
            }

            throw new AppException("Direct membership purchase not yet implemented. Please purchase through listing creation.");

        } catch (Exception e) {
            log.error("Lỗi thanh toán membership: ", e);
            throw new AppException("Lỗi thanh toán membership: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<PaymentResponse> handleMoMoCallback(MomoCallbackRequest request) {
        try {
            log.info("Nhận callback từ MoMo: orderId={}, resultCode={}, message={}",
                    request.getOrderId(), request.getResultCode(), request.getMessage());

            Map<String, String> params = new HashMap<>();
            params.put("partnerCode", request.getPartnerCode());
            params.put("orderId", request.getOrderId());
            params.put("amount", String.valueOf(request.getAmount()));
            params.put("resultCode", String.valueOf(request.getResultCode()));

            boolean isValid = moMoService.verifyCallback(params);

            if (!isValid) {
                log.error("Chữ ký MoMo không hợp lệ: {}", request.getSignature());
                throw new AppException("Chữ ký không hợp lệ");
            }

            Long paymentId = Long.parseLong(request.getOrderId());
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Payment không tồn tại"));

            if (request.isSuccess()) {
                payment.setPaymentStatus("SUCCESS");
                payment.setGatewayTransactionId(request.getGatewayTransactionId());
                payment.setPaymentDate(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());

                handlePostPaymentActions(payment);

                log.info("Thanh toán MoMo thành công: paymentId={}, transactionId={}",
                        paymentId, request.getGatewayTransactionId());
            } else {
                payment.setPaymentStatus("FAILED");
                payment.setUpdatedAt(LocalDateTime.now());
                log.warn("Thanh toán MoMo thất bại: paymentId={}, resultCode={}, message={}",
                        paymentId, request.getResultCode(), request.getMessage());
            }

            paymentRepository.save(payment);

            return BaseResponse.success(toPaymentResponse(payment), "Xử lý thanh toán MoMo thành công");

        } catch (Exception e) {
            log.error("Lỗi xử lý callback MoMo: ", e);
            throw new AppException("Lỗi xử lý thanh toán MoMo: " + e.getMessage());
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

        log.info("Kích hoạt membership thành công: userId={}, membershipId={}",
                user.getUserId(), membership.getListingPackageId());
    }

    private void handleContractPaymentSuccess(Payment payment) {
        Contract contract = contractRepository.findById(payment.getContractId())
                .orElseThrow(() -> new AppException("Contract not found"));

        contract.setContractStatus("PAID");
        contractRepository.save(contract);

        if (contract.getOrder() != null) {
            log.info("Cập nhật trạng thái order thành PAID: orderId={}", contract.getOrder().getOrderId());
        }
    }

    private void handleAddOnPaymentSuccess(Payment payment) {
        ContractAddOn contractAddOn = contractAddOnRepository.findById(payment.getContractAddOnId())
                .orElseThrow(() -> new AppException("Contract addon not found"));

        log.info("Addon payment completed: contractAddOnId={}, amount={}",
                contractAddOn.getId(), contractAddOn.getFee());

        log.info("Addon dịch vụ {} đã được thanh toán thành công",
                contractAddOn.getService().getName());
    }

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

    @Override
    public BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Thanh toán không tồn tại"));

            if (payment.getPayer().getUserId() != user.getUserId()) {
                throw new AppException("Bạn không có quyền xem thanh toán này");
            }

            return BaseResponse.success(toPaymentResponse(payment), "Chi tiết thanh toán");
        } catch (Exception e) {
            log.error("Lỗi lấy chi tiết thanh toán: ", e);
            throw new AppException("Lỗi lấy chi tiết thanh toán");
        }
    }

    private Payment createPayment(Payment.PaymentType type, BigDecimal amount, User payer) {
        Payment payment = new Payment();
        payment.setPaymentType(type);
        payment.setAmount(amount);
        payment.setPayer(payer);
        payment.setPaymentGateway("MOMO");
        payment.setPaymentStatus("PENDING");
        payment.setCurrency("VND");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setExpiryTime(LocalDateTime.now().plusMinutes(15));
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