package com.evmarket.trade.service;

import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.VNPayCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface PaymentService {
    BaseResponse<PaymentResponse> payListingPackageWithVNPay(Long listingPackageId, User payer, String ipAddress);
    BaseResponse<PaymentResponse> payMembershipWithVNPay(Long servicePackageId, User payer, String ipAddress);
    BaseResponse<PaymentResponse> payContractAddOnWithVNPay(Long contractAddOnId, User payer, String ipAddress);
    BaseResponse<PaymentResponse> payContractAddonsWithVNPay(Long contractId, User payer, String ipAddress);
    BaseResponse<PaymentResponse> handleVNPayCallback(VNPayCallbackRequest request);
    BaseResponse<List<ServicePackage>> getMembershipPackages();
    BaseResponse<List<ServicePackage>> getListingVipPackages();
    BaseResponse<List<PaymentResponse>> getMyPayments(User user);
    BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user);
}