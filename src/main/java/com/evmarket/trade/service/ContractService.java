package com.evmarket.trade.service;

import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.ContractSignRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ContractResponse;

import java.util.List;

public interface ContractService {
    
    // Contract generation
    BaseResponse<ContractResponse> generateContract(Long orderId, User user);
    BaseResponse<List<ContractResponse>> getContractsOfMyOrders(User user);
    BaseResponse<ContractResponse> getContractById(Long contractId, User user);
    
    // Contract signing with OTP
    BaseResponse<ContractResponse> signContract(ContractSignRequest request, User user);
    BaseResponse<String> sendOTP(Long contractId, User user);
    
    // Contract management
    BaseResponse<List<ContractResponse>> getMyContracts(User user);
    BaseResponse<Void> cancelContract(Long contractId, User user);
}
