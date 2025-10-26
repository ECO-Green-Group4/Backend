package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ContractAddOnResponse;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.service.AddOnServiceInterface;
import com.evmarket.trade.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private PaymentService paymentService;

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


    // Contract AddOn management
    @Override
    public BaseResponse<ContractAddOnResponse> createContractAddOn(ContractAddOnRequest request, User user) {
        try {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new AppException("Contract not found"));

            // Check if user is staff or admin
            if (!"STAFF".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                throw new AppException("Only staff can add services to contracts");
            }

            // Check if contract is in a valid state for adding add-ons
            // Allow adding add-ons to contracts that are DRAFT, PENDING_SIGNATURE, or SIGNED
            String contractStatus = contract.getContractStatus();
            if (contractStatus == null || 
                (!"DRAFT".equals(contractStatus) && 
                 !"PENDING_SIGNATURE".equals(contractStatus) && 
                 !"SIGNED".equals(contractStatus))) {
                throw new AppException("Add-on services can only be added to contracts in DRAFT, PENDING_SIGNATURE, or SIGNED status");
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
            contractAddOn.setFee(service.getDefaultFee());
            contractAddOn.setCreatedAt(LocalDateTime.now());
            contractAddOn.setPaymentStatus("PENDING"); // Trạng thái thanh toán mặc định

            ContractAddOn saved = contractAddOnRepository.save(contractAddOn);
            
            // Cập nhật danh sách addonIds trong contract
            updateContractAddonIds(contract, saved.getId());
            
            return BaseResponse.success(toResponse(saved), "Contract add-on created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create contract add-on: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<ContractAddOnResponse>> getContractAddOns(Long contractId, User user) {
        try {
            Contract contract = contractRepository.findById(contractId)
                    .orElseThrow(() -> new AppException("Contract not found"));

            // Check if user is authorized to view this contract's add-ons
            boolean isAuthorized = "STAFF".equals(user.getRole()) || "ADMIN".equals(user.getRole()) ||
                    contract.getOrder().getBuyer().getUserId() == user.getUserId() ||
                    contract.getOrder().getSeller().getUserId() == user.getUserId();
            
            if (!isAuthorized) {
                throw new AppException("You can only view add-ons for your own contracts");
            }

            List<ContractAddOn> contractAddOns = contractAddOnRepository.findByContract(contract);
            List<ContractAddOnResponse> responses = contractAddOns.stream().map(this::toResponse).collect(Collectors.toList());
            return BaseResponse.success(responses, "Contract add-ons retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve contract add-ons: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ContractAddOnResponse> getContractAddOnById(Long contractAddOnId, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));

            // Check if user is authorized to view this add-on
            Contract contract = contractAddOn.getContract();
            boolean isAuthorized = "STAFF".equals(user.getRole()) || "ADMIN".equals(user.getRole()) ||
                    contract.getOrder().getBuyer().getUserId() == user.getUserId() ||
                    contract.getOrder().getSeller().getUserId() == user.getUserId();
            
            if (!isAuthorized) {
                throw new AppException("You can only view your own contract add-ons");
            }

            return BaseResponse.success(toResponse(contractAddOn), "Contract add-on retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve contract add-on: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> deleteContractAddOn(Long contractAddOnId, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));

            // Check if user is staff or admin
            if (!"STAFF".equals(user.getRole()) && !"ADMIN".equals(user.getRole())) {
                throw new AppException("Only staff can delete contract add-ons");
            }

            // Lưu contract và addonId trước khi xóa
            Contract contract = contractAddOn.getContract();
            Long addonId = contractAddOn.getId();
            
            contractAddOnRepository.delete(contractAddOn);
            
            // Xóa addonId khỏi danh sách trong contract
            removeContractAddonId(contract, addonId);
            
            return BaseResponse.success(null, "Contract add-on deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete contract add-on: " + e.getMessage());
        }
    }

    private ContractAddOnResponse toResponse(ContractAddOn entity) {
        ContractAddOnResponse r = new ContractAddOnResponse();
        r.setId(entity.getId());
        r.setContractId(entity.getContract() != null ? entity.getContract().getContractId() : null);
        r.setServiceId(entity.getService() != null ? entity.getService().getServiceId() : null);
        r.setServiceName(entity.getService() != null ? entity.getService().getName() : null);
        r.setFee(entity.getFee());
        r.setCreatedAt(entity.getCreatedAt());
        r.setPaymentStatus(entity.getPaymentStatus());
        return r;
    }



    // THÊM PHƯƠNG THỨC MỚI: Get payments by specific contract addon
    public BaseResponse<List<PaymentResponse>> getPaymentsByContractAddOn(Long contractAddOnId, User user) {
        try {
            ContractAddOn contractAddOn = contractAddOnRepository.findById(contractAddOnId)
                    .orElseThrow(() -> new AppException("Contract add-on not found"));

            // Check if user is either buyer or seller
            if (contractAddOn.getContract().getOrder().getBuyer().getUserId() != user.getUserId() &&
                    contractAddOn.getContract().getOrder().getSeller().getUserId() != user.getUserId()) {
                throw new AppException("You can only view payments for your own contract add-ons");
            }

            // Tìm payments theo contractAddOnId
            List<Payment> payments = paymentRepository.findByContractAddOnId(contractAddOnId);

            // Convert sang PaymentResponse
            List<PaymentResponse> responses = payments.stream()
                    .map(this::toPaymentResponse)
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Payments for contract add-on retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve payments for contract add-on: " + e.getMessage());
        }
    }

    // Helper method để convert Payment -> PaymentResponse
    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .paymentType(payment.getPaymentType() != null ? payment.getPaymentType().toString() : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getPaymentStatus())
                .paymentDate(payment.getPaymentDate())
                .expiryTime(payment.getExpiryTime())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .contractId(payment.getContractId()) // Thêm contractId
                .contractAddOnId(payment.getContractAddOnId()) // Thêm contractAddOnId
                .build();
    }

    // Helper method để cập nhật danh sách addonIds trong contract
    private void updateContractAddonIds(Contract contract, Long addonId) {
        List<Long> addonIds = contract.getAddonIds();
        if (addonIds == null) {
            addonIds = new ArrayList<>();
        }
        if (!addonIds.contains(addonId)) {
            addonIds.add(addonId);
            contract.setAddonIds(addonIds);
            contractRepository.save(contract);
        }
    }

    // Helper method để xóa addonId khỏi danh sách trong contract
    private void removeContractAddonId(Contract contract, Long addonId) {
        List<Long> addonIds = contract.getAddonIds();
        if (addonIds != null && addonIds.contains(addonId)) {
            addonIds.remove(addonId);
            contract.setAddonIds(addonIds);
            contractRepository.save(contract);
        }
    }

}