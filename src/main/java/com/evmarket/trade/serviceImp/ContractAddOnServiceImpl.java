package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.ContractAddOn;
import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.AddOnService;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.ContractAddOnRepository;
import com.evmarket.trade.repository.ContractRepository;
import com.evmarket.trade.repository.AddOnServiceRepository;
import com.evmarket.trade.request.CreateContractAddOnRequest;
import com.evmarket.trade.service.ContractAddOnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ContractAddOnServiceImpl implements ContractAddOnService {

    @Autowired
    private ContractAddOnRepository contractAddOnRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private AddOnServiceRepository addOnServiceRepository;

    @Override
    public ContractAddOn createContractAddOn(CreateContractAddOnRequest request, User user) {
        Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + request.getContractId()));
        
        // Check if contract is signed
        if (!"SIGNED".equals(contract.getContractStatus())) {
            throw new RuntimeException("Contract must be signed before adding services");
        }
        
        // Check if user is authorized (either buyer or seller)
        boolean isAuthorized = user.getUserId() == contract.getOrder().getBuyer().getUserId() ||
                              user.getUserId() == contract.getOrder().getSeller().getUserId();
        
        if (!isAuthorized) {
            throw new RuntimeException("User is not authorized to add services to this contract");
        }
        
        AddOnService service = addOnServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Add-on service not found with id: " + request.getServiceId()));
        
        ContractAddOn contractAddOn = new ContractAddOn();
        contractAddOn.setContract(contract);
        contractAddOn.setService(service);
        contractAddOn.setFee(request.getFee());
        contractAddOn.setCreatedAt(LocalDateTime.now());
        
        ContractAddOn savedContractAddOn = contractAddOnRepository.save(contractAddOn);
        
        // Generate payment for the add-on service
        generatePaymentForAddOn(savedContractAddOn.getId());
        
        return savedContractAddOn;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractAddOn> getContractAddOnsByContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));
        return contractAddOnRepository.findByContract(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractAddOn getContractAddOnById(Long id) {
        return contractAddOnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract add-on not found with id: " + id));
    }

    @Override
    public void generatePaymentForAddOn(Long contractAddOnId) {
        ContractAddOn contractAddOn = getContractAddOnById(contractAddOnId);
        
        // Create payment request for the add-on service
        // This would typically trigger a payment creation process
        // For demo purposes, we'll just log it
        System.out.println("Payment generated for ContractAddOn ID: " + contractAddOnId + 
                          ", Amount: " + contractAddOn.getFee() + 
                          ", Service: " + contractAddOn.getService().getName());
    }
}
