package com.evmarket.trade.service;

import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.SelectPackageRequest;
import com.evmarket.trade.response.ServicePackageResponse;

import java.util.List;

public interface ServicePackageService {
    List<ServicePackageResponse> getAllActivePackages();
    ServicePackageResponse getPackageById(Long packageId);
    void applyPackageToListing(SelectPackageRequest request, User user);
}
