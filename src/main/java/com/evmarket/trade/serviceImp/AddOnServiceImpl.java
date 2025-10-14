package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AddOnServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AddOnServiceImpl implements AddOnServiceInterface {
    
    @Autowired
    private AddOnServiceRepository addOnServiceRepository;
    
    @Autowired
    private ContractAddOnRepository contractAddOnRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // AddOn service management
    @Override
    public BaseResponse<List<AddOnService>> getAvailableAddOnServices() {
        try {
            List<AddOnService> services = addOnServiceRepository.findByStatus("ACTIVE");
            return BaseResponse.success(services, "Available add-on services retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve add-on services: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<AddOnService> getAddOnServiceById(Long serviceId) {
        try {
            AddOnService service = addOnServiceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException("Add-on service not found"));
            
            return BaseResponse.success(service, "Add-on service retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve add-on service: " + e.getMessage());
        }
    }
    
    // Contract AddOn management
    @Override
    public BaseResponse<ContractAddOn> createContractAddOn(ContractAddOnRequest request, User user) {
        try {
            Contract contract = contractRepository.findById(request.getContractId())
                .orElseThrow(() -> new AppException("Contract not found"));
            
            // Check if user is either buyer or seller
            if (contract.getOrder().getBuyer().getUserId() != user.getUserId() && 
                contract.getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only add services to your own contracts");
            }
            
            // Check if contract is signed
            if (!"SIGNED".equals(contract.getContractStatus())) {
                throw new AppException("Add-on services can only be added to signed contracts");
            }
            
            AddOnService service = addOnServiceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new AppException("Add-on service not found"));
            
            if (!"ACTIVE".equals(service.getStatus())) {
                throw new AppException("Add-on service is not available");
            }
            
            // Check if this service is already added to the contract
            List<ContractAddOn> existingAddOns = contractAddOnRepository.findByContractAndService(contract, service);
            if (!existingAddOns.isEmpty()) {
                throw new AppException("This service is already added to the contract");
            }
            
            ContractAddOn contractAddOn = new ContractAddOn();
            contractAddOn.setContract(contract);
            contractAddOn.setService(service);
            contractAddOn.setFee(request.getFee());
            contractAddOn.setCreatedAt(LocalDateTime.now());
            
            ContractAddOn savedContractAddOn = contractAddOnRepository.save(contractAddOn);
            return BaseResponse.success(savedContractAddOn, "Contract add-on created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create contract add-on: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<ContractAddOn>> getContractAddOns(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException("Contract not found"));
            
            // Check if user is either buyer or seller
            if (contract.getOrder().getBuyer().getUserId() != user.getUserId() && 
                contract.getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only view add-ons for your own contracts");
            }
            
            List<ContractAddOn> contractAddOns = contractAddOnRepository.findByContract(contract);
            return BaseResponse.success(contractAddOns, "Contract add-ons retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve contract add-ons: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<ContractAddOn> getContractAddOnById(Long contractAddOnId, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                .orElseThrow(() -> new AppException("Contract add-on not found"));
            
            // Check if user is either buyer or seller
            if (contractAddOn.getContract().getOrder().getBuyer().getUserId() != user.getUserId() && 
                contractAddOn.getContract().getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only view your own contract add-ons");
            }
            
            return BaseResponse.success(contractAddOn, "Contract add-on retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve contract add-on: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<Void> deleteContractAddOn(Long contractAddOnId, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                .orElseThrow(() -> new AppException("Contract add-on not found"));
            
            // Check if user is either buyer or seller
            if (contractAddOn.getContract().getOrder().getBuyer().getUserId() != user.getUserId() && 
                contractAddOn.getContract().getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only delete your own contract add-ons");
            }
            
            contractAddOnRepository.delete(contractAddOn);
            return BaseResponse.success(null, "Contract add-on deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete contract add-on: " + e.getMessage());
        }
    }
    
    // AddOn payment management
    @Override
    public BaseResponse<Payment> processAddOnPayment(AddOnPaymentRequest request, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(request.getContractAddOnId())
                .orElseThrow(() -> new AppException("Contract add-on not found"));
            
            // Check if user is either buyer or seller
            if (contractAddOn.getContract().getOrder().getBuyer().getUserId() != user.getUserId() && 
                contractAddOn.getContract().getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only pay for your own contract add-ons");
            }
            
            // Validate payment amount
            if (request.getAmount().compareTo(contractAddOn.getFee()) != 0) {
                throw new AppException("Payment amount does not match add-on fee");
            }
            
            Payment payment = new Payment();
            payment.setContract(contractAddOn.getContract());
            payment.setPayer(user);
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setAmount(request.getAmount());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setStatus("SUCCESS"); // In real implementation, integrate with payment gateway
            
            Payment savedPayment = paymentRepository.save(payment);
            
            return BaseResponse.success(savedPayment, "Add-on payment processed successfully");
        } catch (Exception e) {
            throw new AppException("Failed to process add-on payment: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<Payment>> getAddOnPayments(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException("Contract not found"));
            
            // Check if user is either buyer or seller
            if (contract.getOrder().getBuyer().getUserId() != user.getUserId() && 
                contract.getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only view payments for your own contracts");
            }
            
            List<Payment> payments = paymentRepository.findByContract(contract);
            return BaseResponse.success(payments, "Add-on payments retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve add-on payments: " + e.getMessage());
        }
    }
}

