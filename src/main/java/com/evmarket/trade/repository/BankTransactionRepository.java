package com.evmarket.trade.repository;

import com.evmarket.trade.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    
    /**
     * Tìm giao dịch theo mã giao dịch ngân hàng
     */
    Optional<BankTransaction> findByCode(String code);
    
    /**
     * Tìm giao dịch theo nội dung chuyển khoản (description)
     */
    List<BankTransaction> findByDescriptionContaining(String description);
    
    /**
     * Tìm giao dịch theo payment ID
     */
    List<BankTransaction> findByPaymentId(Long paymentId);
    
    /**
     * Tìm giao dịch theo trạng thái
     */
    List<BankTransaction> findByStatus(String status);
    
    /**
     * Tìm giao dịch theo khoảng thời gian
     */
    List<BankTransaction> findByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Tìm giao dịch theo số tài khoản
     */
    List<BankTransaction> findByAccountNumber(String accountNumber);
}


