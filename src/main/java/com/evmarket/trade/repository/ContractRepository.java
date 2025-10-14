package com.evmarket.trade.repository;

import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Contract findByOrder(Order order);
    List<Contract> findByContractStatus(String contractStatus);
    
    @Query("SELECT c FROM Contract c WHERE c.order = :order")
    Contract findByOrderId(@Param("order") Order order);
}
