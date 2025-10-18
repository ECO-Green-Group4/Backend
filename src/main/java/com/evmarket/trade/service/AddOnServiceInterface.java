package com.evmarket.trade.service;

import com.evmarket.trade.entity.AddOnService;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.AddOnPaymentRequest;
import com.evmarket.trade.request.ContractAddOnRequest;
import com.evmarket.trade.response.ContractAddOnResponse;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface AddOnServiceInterface {
    // AddOn service management
    BaseResponse<List<AddOnService>> getAvailableAddOnServices();
    BaseResponse<AddOnService> getAddOnServiceById(Long serviceId);

    // Contract AddOn management
    BaseResponse<ContractAddOnResponse> createContractAddOn(ContractAddOnRequest request, User user);
    BaseResponse<List<ContractAddOnResponse>> getContractAddOns(Long contractId, User user);
    BaseResponse<ContractAddOnResponse> getContractAddOnById(Long contractAddOnId, User user);
    BaseResponse<Void> deleteContractAddOn(Long contractAddOnId, User user);

    // AddOn payment management - CẬP NHẬT: Sử dụng PaymentResponse
    BaseResponse<PaymentResponse> processAddOnPayment(AddOnPaymentRequest request, User user);
    BaseResponse<List<PaymentResponse>> getAddOnPayments(Long contractId, User user);
}