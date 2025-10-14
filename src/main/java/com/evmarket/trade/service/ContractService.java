package com.evmarket.trade.service;

import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.ContractSignRequest;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface ContractService {
    
    // Contract generation
    BaseResponse<Contract> generateContract(Long orderId, User user);
    BaseResponse<Contract> getContractByOrderId(Long orderId, User user);
    BaseResponse<Contract> getContractById(Long contractId, User user);
    
    // Contract signing with OTP
    BaseResponse<Contract> signContract(ContractSignRequest request, User user);
    BaseResponse<String> sendOTP(Long contractId, User user);
    
    // Contract management
    BaseResponse<List<Contract>> getMyContracts(User user);
    BaseResponse<Void> cancelContract(Long contractId, User user);
}
