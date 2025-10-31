package com.evmarket.trade.repository;

import com.evmarket.trade.entity.ContractAddOn;
import com.evmarket.trade.entity.Contract;
import com.evmarket.trade.entity.AddOnService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractAddOnRepository extends JpaRepository<ContractAddOn, Long> {
    List<ContractAddOn> findByContract(Contract contract);
    
    @Query("SELECT cao FROM ContractAddOn cao WHERE cao.contract = :contract AND cao.service = :service")
    List<ContractAddOn> findByContractAndService(@Param("contract") Contract contract, @Param("service") AddOnService service);
}
