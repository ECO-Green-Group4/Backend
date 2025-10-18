package com.evmarket.trade.repository;

import com.evmarket.trade.entity.AddOnService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddOnServiceRepository extends JpaRepository<AddOnService, Long> {
    List<AddOnService> findByStatus(String status);
}
