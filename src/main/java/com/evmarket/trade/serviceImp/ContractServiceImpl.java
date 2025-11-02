package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.ContractRepository;
import com.evmarket.trade.repository.ContractAddOnRepository;
import com.evmarket.trade.repository.OrderRepository;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.request.ContractSignRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ContractResponse;
import com.evmarket.trade.response.ContractDetailsResponse;
import com.evmarket.trade.response.ContractAddOnResponse;
import com.evmarket.trade.service.ContractService;
import com.evmarket.trade.service.EmailService;
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
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private ContractAddOnRepository contractAddOnRepository;
    
    @Autowired
    private EmailService emailService;
    
    private final Map<Long, String> otpStorage = new HashMap<>();

    @Override
    public BaseResponse<ContractResponse> generateContract(Long orderId, User user) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            
            // Check if user is authorized (only staff can generate contracts)
            if (!"STAFF".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                throw new RuntimeException("Only staff can generate contracts");
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

            // Disallow signing if contract is already fully signed or cancelled
            if ("SIGNED".equals(contract.getContractStatus())) {
                throw new RuntimeException("Contract has already been fully signed");
            }
            if ("CANCELLED".equals(contract.getContractStatus())) {
                throw new RuntimeException("Cannot sign a cancelled contract");
            }
            
            // Update contract status and signature flags
            if ("DRAFT".equals(contract.getContractStatus())) {
                contract.setContractStatus("PENDING_SIGNATURE");
                // First signature - set the appropriate flag
                if (isBuyer) {
                    contract.setSignedByBuyer(true);
                } else if (isSeller) {
                    contract.setSignedBySeller(true);
                }
            } else if ("PENDING_SIGNATURE".equals(contract.getContractStatus())) {
                // Second signature - set the remaining flag and mark as fully signed
                if (isBuyer && (contract.getSignedByBuyer() == null || !contract.getSignedByBuyer())) {
                    contract.setSignedByBuyer(true);
                } else if (isSeller && (contract.getSignedBySeller() == null || !contract.getSignedBySeller())) {
                    contract.setSignedBySeller(true);
                }
                
                // Check if both parties have signed
                if (Boolean.TRUE.equals(contract.getSignedByBuyer()) && Boolean.TRUE.equals(contract.getSignedBySeller())) {
                    contract.setContractStatus("SIGNED");
                    
                    // When contract is fully signed, mark order as COMPLETED and take down the listing
                    Listing listing = order.getListing();
                    if (listing != null) {
                        listing.setStatus("SOLD");
                        listing.setUpdatedAt(java.time.LocalDateTime.now());
                        listingRepository.save(listing);
                    }
                    
                    // Update order status
                    order.setStatus("COMPLETED");
                    orderRepository.save(order);
                }
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
            // Send OTP to the authenticated user's email
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new RuntimeException("User does not have an email to receive OTP");
            }
            emailService.sendContractOtpEmail(user.getEmail(), otp);
            return BaseResponse.success("OK", "OTP has been sent to your email");
            
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
    public BaseResponse<List<ContractDetailsResponse>> getMyContractDetails(User user) {
        try {
            // Lấy các hợp đồng mà user là buyer hoặc seller
            List<Contract> contracts = contractRepository.findAll().stream()
                    .filter(contract -> {
                        Order order = contract.getOrder();
                        return user.getUserId() == order.getBuyer().getUserId() ||
                               user.getUserId() == order.getSeller().getUserId();
                    })
                    .collect(Collectors.toList());

            List<ContractDetailsResponse> responses = contracts.stream()
                    .map(contract -> {
                        List<ContractAddOnResponse> addons = contractAddOnRepository.findByContract(contract)
                                .stream()
                                .map(this::toContractAddOnResponse)
                                .collect(Collectors.toList());

                        Order order = contract.getOrder();
                        return ContractDetailsResponse.builder()
                                .contractId(contract.getContractId())
                                .orderId(order != null ? order.getOrderId() : null)
                                .buyerId(order != null && order.getBuyer() != null ? Long.valueOf(order.getBuyer().getUserId()) : null)
                                .sellerId(order != null && order.getSeller() != null ? Long.valueOf(order.getSeller().getUserId()) : null)
                                .status(contract.getContractStatus())
                                .sellerSigned(Boolean.TRUE.equals(contract.getSignedBySeller()))
                                .buyerSigned(Boolean.TRUE.equals(contract.getSignedByBuyer()))
                                .signedAt(contract.getSignedAt())
                                .createdAt(contract.getSignedAt())
                                .addons(addons)
                                .build();
                    })
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Contract details retrieved successfully");
        } catch (Exception e) {
            return BaseResponse.error("Failed to get contract details: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ContractDetailsResponse> getContractDetailsById(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + contractId));

            // Authorization: user must be buyer or seller of the order
            Order order = contract.getOrder();
            if (order == null || (user.getUserId() != order.getBuyer().getUserId() && user.getUserId() != order.getSeller().getUserId())) {
                throw new RuntimeException("User is not authorized to view this contract");
            }

            List<ContractAddOnResponse> addons = contractAddOnRepository.findByContract(contract)
                    .stream()
                    .map(this::toContractAddOnResponse)
                    .collect(Collectors.toList());

            ContractDetailsResponse response = ContractDetailsResponse.builder()
                    .contractId(contract.getContractId())
                    .orderId(order.getOrderId())
                    .buyerId(order.getBuyer() != null ? Long.valueOf(order.getBuyer().getUserId()) : null)
                    .sellerId(order.getSeller() != null ? Long.valueOf(order.getSeller().getUserId()) : null)
                    .status(contract.getContractStatus())
                    .sellerSigned(Boolean.TRUE.equals(contract.getSignedBySeller()))
                    .buyerSigned(Boolean.TRUE.equals(contract.getSignedByBuyer()))
                    .signedAt(contract.getSignedAt())
                    .createdAt(contract.getSignedAt())
                    .addons(addons)
                    .build();

            return BaseResponse.success(response, "Contract details retrieved successfully");
        } catch (Exception e) {
            return BaseResponse.error("Failed to get contract details: " + e.getMessage());
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
        return ContractResponse.builder()
                .contractId(contract.getContractId())
                .orderId(order != null ? order.getOrderId() : null)
                .status(contract.getContractStatus())
                .sellerSigned(contract.getSignedBySeller() != null ? contract.getSignedBySeller() : false)
                .buyerSigned(contract.getSignedByBuyer() != null ? contract.getSignedByBuyer() : false)
                .signedAt(contract.getSignedAt())
                .createdAt(contract.getSignedAt())
                .build();
    }

    private ContractAddOnResponse toContractAddOnResponse(com.evmarket.trade.entity.ContractAddOn entity) {
        ContractAddOnResponse r = new ContractAddOnResponse();
        r.setId(entity.getId());
        r.setContractId(entity.getContract() != null ? entity.getContract().getContractId() : null);
        r.setServiceId(entity.getService() != null ? entity.getService().getServiceId() : null);
        r.setServiceName(entity.getService() != null ? entity.getService().getName() : null);
        r.setFee(entity.getFee());
        r.setCreatedAt(entity.getCreatedAt());
        r.setPaymentStatus(entity.getPaymentStatus());
        r.setChargedTo(entity.getChargedTo());
        return r;
    }
}