package com.evmarket.trade.service;

import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface PaymentService {
    BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer);
    BaseResponse<PaymentResponse> payMembership(Long servicePackageId, User payer);
    BaseResponse<PaymentResponse> payContract(Long contractId, User payer);
    BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer);
    BaseResponse<List<ServicePackage>> getMembershipPackages();
    BaseResponse<List<ServicePackage>> getListingVipPackages();
    BaseResponse<List<PaymentResponse>> getMyPayments(User user);
    BaseResponse<PaymentResponse> getPaymentById(Long paymentId, User user);
}