package com.evmarket.trade.repository;

import com.evmarket.trade.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {

    List<ServicePackage> findByStatus(String status);

    @Query("SELECT sp FROM ServicePackage sp WHERE sp.packageType = :packageType AND sp.status = 'ACTIVE'")
    List<ServicePackage> findByPackageTypeAndStatusActive(@Param("packageType") ServicePackage.PackageType packageType);

    List<ServicePackage> findByPackageType(ServicePackage.PackageType packageType);

    @Query("SELECT sp FROM ServicePackage sp WHERE sp.packageType = :packageType AND sp.status = :status")
    List<ServicePackage> findByPackageTypeAndStatus(@Param("packageType") ServicePackage.PackageType packageType, @Param("status") String status);
}