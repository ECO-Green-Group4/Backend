package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.VNPayCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface PaymentService {

    // 1. Thanh toán gói tin VIP
    BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer);

    // 2. Thanh toán membership
    BaseResponse<PaymentResponse> payMembership(Long servicePackageId, User payer);

    // 3. Thanh toán hợp đồng xe
    BaseResponse<PaymentResponse> payContract(Long contractId, User payer);

    // 4. Thanh toán addon hợp đồng
    BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer);

    // Xử lý callback từ VNPay
    BaseResponse<PaymentResponse> handleVNPayCallback(VNPayCallbackRequest request);

    // Common methods
    BaseResponse<List<PaymentResponse>> getMyPayments(User user);
    BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user);

    // Lấy danh sách gói membership
    BaseResponse<List<com.evmarket.trade.entity.ServicePackage>> getMembershipPackages();

    // Lấy danh sách gói tin VIP
    BaseResponse<List<com.evmarket.trade.entity.ServicePackage>> getListingVipPackages();
}