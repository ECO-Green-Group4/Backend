package com.evmarket.trade.service;

import com.evmarket.trade.entity.Battery;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateBatteryRequest;
import com.evmarket.trade.response.BatteryResponse;

import java.util.List;

public interface BatteryService {
    Battery createBattery(CreateBatteryRequest request, User seller);
    List<BatteryResponse> getBatteriesBySeller(User seller);
    List<BatteryResponse> getAllAvailableBatteries();
    BatteryResponse getBatteryById(Long batteryId);
    Battery updateBatteryStatus(Long batteryId, String status);
}
