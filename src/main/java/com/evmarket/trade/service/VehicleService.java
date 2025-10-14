package com.evmarket.trade.service;

import com.evmarket.trade.entity.Vehicle;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateVehicleRequest;
import com.evmarket.trade.response.VehicleResponse;

import java.util.List;

public interface VehicleService {
    Vehicle createVehicle(CreateVehicleRequest request, User seller);
    List<VehicleResponse> getVehiclesBySeller(User seller);
    List<VehicleResponse> getAllAvailableVehicles();
    VehicleResponse getVehicleById(Long vehicleId);
    Vehicle updateVehicleStatus(Long vehicleId, String status);
}
