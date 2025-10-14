package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Battery;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.BatteryRepository;
import com.evmarket.trade.request.CreateBatteryRequest;
import com.evmarket.trade.response.BatteryResponse;
import com.evmarket.trade.service.BatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BatteryServiceImpl implements BatteryService {

    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public Battery createBattery(CreateBatteryRequest request, User seller) {
        Battery battery = new Battery();
        battery.setSeller(seller);
        battery.setType(request.getType());
        battery.setCapacity(request.getCapacity());
        battery.setHealthPercent(request.getHealthPercent());
        battery.setManufactureYear(request.getManufactureYear());
        battery.setPrice(request.getPrice());
        battery.setStatus("AVAILABLE");
        battery.setCreatedAt(LocalDateTime.now());
        
        return batteryRepository.save(battery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatteryResponse> getBatteriesBySeller(User seller) {
        List<Battery> batteries = batteryRepository.findBySeller(seller);
        return batteries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BatteryResponse> getAllAvailableBatteries() {
        List<Battery> batteries = batteryRepository.findByStatus("AVAILABLE");
        return batteries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BatteryResponse getBatteryById(Long batteryId) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new RuntimeException("Battery not found with id: " + batteryId));
        return convertToResponse(battery);
    }

    @Override
    public Battery updateBatteryStatus(Long batteryId, String status) {
        Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new RuntimeException("Battery not found with id: " + batteryId));
        battery.setStatus(status);
        return batteryRepository.save(battery);
    }

    private BatteryResponse convertToResponse(Battery battery) {
        return new BatteryResponse(
                battery.getBatteryId(),
                battery.getType(),
                battery.getCapacity(),
                battery.getHealthPercent(),
                battery.getManufactureYear(),
                battery.getPrice(),
                battery.getStatus(),
                battery.getCreatedAt(),
                battery.getSeller().getFullName(),
                battery.getSeller().getPhone()
        );
    }
}
