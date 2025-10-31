package com.evmarket.trade.service;

import com.evmarket.trade.entity.ContractAddOn;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateContractAddOnRequest;

import java.util.List;

public interface ContractAddOnService {
    ContractAddOn createContractAddOn(CreateContractAddOnRequest request, User user);
    List<ContractAddOn> getContractAddOnsByContract(Long contractId);
    ContractAddOn getContractAddOnById(Long id);
    void generatePaymentForAddOn(Long contractAddOnId);
}
