package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Payment;
import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPayer(User payer);
    List<Payment> findByContract(Contract contract);
    List<Payment> findByListingPackage(ListingPackage listingPackage);
    List<Payment> findByStatus(String status);
    
    @Query("SELECT p FROM Payment p WHERE p.payer = :payer AND p.status = :status")
    List<Payment> findByPayerAndStatus(@Param("payer") User payer, @Param("status") String status);
}