package com.evmarket.trade.repository;



import com.evmarket.trade.entity.Payment;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPayer(User payer);

    List<Payment> findByPaymentStatus(String paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.payer = :payer AND p.paymentStatus = :status ORDER BY p.createdAt DESC")
    List<Payment> findByPayerAndStatus(@Param("payer") User payer, @Param("status") String status);

    @Query("SELECT p FROM Payment p WHERE p.payer = :payer ORDER BY p.createdAt DESC")
    List<Payment> findByPayerOrderByCreatedAtDesc(@Param("payer") User payer);

    List<Payment> findByListingPackageId(Long listingPackageId);

    List<Payment> findByListingPackageIdAndPaymentStatus(Long listingPackageId, String paymentStatus);

    List<Payment> findByContractId(Long contractId);

    List<Payment> findByContractIdAndPaymentStatus(Long contractId, String paymentStatus);

    List<Payment> findByContractAddOnId(Long contractAddOnId);

    List<Payment> findByContractAddOnIdAndPaymentStatus(Long contractAddOnId, String paymentStatus);

    @Query("SELECT p FROM Payment p WHERE p.payer = :payer AND p.paymentType = :paymentType AND p.paymentStatus = :paymentStatus ORDER BY p.createdAt DESC")
    List<Payment> findByPayerAndPaymentTypeAndPaymentStatus(@Param("payer") User payer, @Param("paymentType") Payment.PaymentType paymentType, @Param("paymentStatus") String paymentStatus);

    // Find payment by gateway transaction ID (for Stripe sessionId, MoMo orderId, etc.)
    @Query("SELECT p FROM Payment p WHERE p.gatewayTransactionId = :transactionId")
    java.util.Optional<Payment> findByGatewayTransactionId(@Param("transactionId") String transactionId);
}