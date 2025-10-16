package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface PaymentService {
    BaseResponse<PaymentResponse> payListingPackage(Long listingPackageId, User payer);
    BaseResponse<PaymentResponse> payContract(Long contractId, User payer);
    BaseResponse<PaymentResponse> payContractAddOn(Long contractAddOnId, User payer);

    BaseResponse<List<PaymentResponse>> getMyPayments(User user);
    BaseResponse<List<PaymentResponse>> getPaymentsByContract(Long contractId, User user);
    BaseResponse<List<PaymentResponse>> getPaymentsByListingPackage(Long listingPackageId, User user);
}

