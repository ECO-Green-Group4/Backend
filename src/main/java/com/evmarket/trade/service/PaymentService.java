package com.evmarket.trade.service;

import com.evmarket.trade.entity.Payment;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreatePaymentRequest;

import java.util.List;

public interface PaymentService {
    Payment createPayment(CreatePaymentRequest request, User payer);
    List<Payment> getPaymentsByUser(User user);
    Payment getPaymentById(Long paymentId);
    Payment updatePaymentStatus(Long paymentId, String status);
    void processPackagePayment(Long listingPackageId, User user);
    void processContractAddOnPayment(Long contractAddOnId, User user);
}
