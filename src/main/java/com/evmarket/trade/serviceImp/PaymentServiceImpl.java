package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Payment;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.repository.PaymentRepository;
import com.evmarket.trade.repository.ContractRepository;
import com.evmarket.trade.repository.ListingPackageRepository;
import com.evmarket.trade.request.CreatePaymentRequest;
import com.evmarket.trade.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private ListingPackageRepository listingPackageRepository;

    @Override
    public Payment createPayment(CreatePaymentRequest request, User payer) {
        Payment payment = new Payment();
        payment.setPayer(payer);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("PENDING");
        
        // Set contract or listing package based on request
        if (request.getContractId() != null) {
            Contract contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new RuntimeException("Contract not found with id: " + request.getContractId()));
            payment.setContract(contract);
        }
        
        if (request.getListingPackageId() != null) {
            ListingPackage listingPackage = listingPackageRepository.findById(request.getListingPackageId())
                    .orElseThrow(() -> new RuntimeException("Listing package not found with id: " + request.getListingPackageId()));
            payment.setListingPackage(listingPackage);
        }
        
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByUser(User user) {
        return paymentRepository.findByPayer(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
    }

    @Override
    public Payment updatePaymentStatus(Long paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    @Override
    public void processPackagePayment(Long listingPackageId, User user) {
        ListingPackage listingPackage = listingPackageRepository.findById(listingPackageId)
                .orElseThrow(() -> new RuntimeException("Listing package not found with id: " + listingPackageId));
        
        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setAmount(listingPackage.getServicePackage().getListingFee());
        paymentRequest.setPaymentMethod("BANK_TRANSFER");
        paymentRequest.setListingPackageId(listingPackageId);
        
        createPayment(paymentRequest, user);
    }

    @Override
    public void processContractAddOnPayment(Long contractAddOnId, User user) {
        // This would be implemented when ContractAddOn service is created
        // For now, just a placeholder
        throw new RuntimeException("ContractAddOn payment processing not implemented yet");
    }
}
