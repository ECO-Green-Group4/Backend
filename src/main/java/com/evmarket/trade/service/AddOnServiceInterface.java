package com.evmarket.trade.service;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ContractAddOnResponse;

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
    
    // AddOn payment management
    BaseResponse<Payment> processAddOnPayment(AddOnPaymentRequest request, User user);
    BaseResponse<List<Payment>> getAddOnPayments(Long contractId, User user);
}



