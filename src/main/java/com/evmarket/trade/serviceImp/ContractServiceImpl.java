package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.ContractRepository;
import com.evmarket.trade.repository.OrderRepository;
import com.evmarket.trade.request.ContractSignRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ContractResponse;
import com.evmarket.trade.service.ContractService;
import com.evmarket.trade.util.OTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    private final Map<Long, String> otpStorage = new HashMap<>();

    @Override
    public BaseResponse<ContractResponse> generateContract(Long orderId, User user) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            
            // Check if user is authorized (either buyer or seller)
            if (user.getUserId() != order.getBuyer().getUserId() && 
                user.getUserId() != order.getSeller().getUserId()) {
                throw new RuntimeException("User is not authorized to generate contract for this order");
            }
            
            // Check if contract already exists
            Contract existingContract = contractRepository.findByOrder(order);
            if (existingContract != null) {
                throw new RuntimeException("Contract already exists for this order");
            }
            
            Contract contract = new Contract();
            contract.setOrder(order);
            contract.setContractStatus("DRAFT");
            contract.setSignedAt(LocalDateTime.now());
            
            Contract savedContract = contractRepository.save(contract);
            return BaseResponse.success(convertToResponse(savedContract), "Contract generated successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to generate contract: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<java.util.List<ContractResponse>> getContractsOfMyOrders(User user) {
        try {
            java.util.List<Contract> contracts = contractRepository.findAll().stream()
                    .filter(c -> {
                        Order o = c.getOrder();
                        return o != null && (user.getUserId() == o.getBuyer().getUserId() || user.getUserId() == o.getSeller().getUserId());
                    })
                    .collect(java.util.stream.Collectors.toList());
            java.util.List<ContractResponse> responses = contracts.stream().map(this::convertToResponse).collect(java.util.stream.Collectors.toList());
            return BaseResponse.success(responses, "Contracts of my orders retrieved successfully");
        } catch (Exception e) {
            return BaseResponse.error("Failed to get contracts: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ContractResponse> getContractById(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));
            
            // Check if user is authorized
            Order order = contract.getOrder();
            if (user.getUserId() != order.getBuyer().getUserId() && 
                user.getUserId() != order.getSeller().getUserId()) {
                throw new RuntimeException("User is not authorized to view this contract");
            }
            
            return BaseResponse.success(convertToResponse(contract), "Contract retrieved successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to get contract: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ContractResponse> signContract(ContractSignRequest request, User user) {
        try {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + request.getContractId()));
            
            // Verify OTP
            if (!verifyOTP(request.getContractId(), request.getOtp())) {
                throw new RuntimeException("Invalid OTP");
            }
            
            // Check if user is either buyer or seller
            Order order = contract.getOrder();
            boolean isBuyer = user.getUserId() == order.getBuyer().getUserId();
            boolean isSeller = user.getUserId() == order.getSeller().getUserId();
            
            if (!isBuyer && !isSeller) {
                throw new RuntimeException("User is not authorized to sign this contract");
            }
            
            // Update contract status
            if ("DRAFT".equals(contract.getContractStatus())) {
                contract.setContractStatus("PENDING_SIGNATURE");
            } else if ("PENDING_SIGNATURE".equals(contract.getContractStatus())) {
                contract.setContractStatus("SIGNED");
            }
            
            contract.setSignedAt(LocalDateTime.now());
            Contract savedContract = contractRepository.save(contract);
            return BaseResponse.success(convertToResponse(savedContract), "Contract signed successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to sign contract: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<String> sendOTP(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));
            
            // Check if user is authorized
            Order order = contract.getOrder();
            if (user.getUserId() != order.getBuyer().getUserId() && 
                user.getUserId() != order.getSeller().getUserId()) {
                throw new RuntimeException("User is not authorized to send OTP for this contract");
            }
            
            String otp = generateOTP(contractId);
            return BaseResponse.success(otp, "OTP sent successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to send OTP: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<ContractResponse>> getMyContracts(User user) {
        try {
            // Get contracts where user is either buyer or seller
            List<Contract> contracts = contractRepository.findAll().stream()
                    .filter(contract -> {
                        Order order = contract.getOrder();
                        return user.getUserId() == order.getBuyer().getUserId() ||
                               user.getUserId() == order.getSeller().getUserId();
                    })
                    .collect(Collectors.toList());
            
            List<ContractResponse> responses = contracts.stream().map(this::convertToResponse).collect(Collectors.toList());
            return BaseResponse.success(responses, "Contracts retrieved successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to get contracts: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> cancelContract(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));
            
            // Check if user is authorized
            Order order = contract.getOrder();
            if (user.getUserId() != order.getBuyer().getUserId() && 
                user.getUserId() != order.getSeller().getUserId()) {
                throw new RuntimeException("User is not authorized to cancel this contract");
            }
            
            // Check if contract can be cancelled
            if ("SIGNED".equals(contract.getContractStatus())) {
                throw new RuntimeException("Cannot cancel a signed contract");
            }
            
            contract.setContractStatus("CANCELLED");
            contract.setSignedAt(LocalDateTime.now());
            contractRepository.save(contract);
            
            return BaseResponse.success(null, "Contract cancelled successfully");
            
        } catch (Exception e) {
            return BaseResponse.error("Failed to cancel contract: " + e.getMessage());
        }
    }

    // Helper methods
    private String generateOTP(Long contractId) {
        String otp = OTPUtil.generateOTP();
        otpStorage.put(contractId, otp);
        return otp;
    }

    private boolean verifyOTP(Long contractId, String otp) {
        String storedOTP = otpStorage.get(contractId);
        return storedOTP != null && storedOTP.equals(otp);
    }

    private ContractResponse convertToResponse(Contract contract) {
        if (contract == null) return null;
        Order order = contract.getOrder();
        boolean sellerSigned = "SELLER_SIGNED".equals(contract.getContractStatus()) || "SIGNED".equals(contract.getContractStatus());
        boolean buyerSigned = "BUYER_SIGNED".equals(contract.getContractStatus()) || "SIGNED".equals(contract.getContractStatus());
        return ContractResponse.builder()
                .contractId(contract.getContractId())
                .orderId(order != null ? order.getOrderId() : null)
                .status(contract.getContractStatus())
                .sellerSigned(sellerSigned)
                .buyerSigned(buyerSigned)
                .signedAt(contract.getSignedAt())
                .createdAt(contract.getSignedAt())
                .build();
    }
}