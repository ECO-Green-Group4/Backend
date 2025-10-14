package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Vehicle;
import com.evmarket.trade.repository.VehicleRepository;
import com.evmarket.trade.request.CreateVehicleRequest;
import com.evmarket.trade.response.VehicleResponse;
import com.evmarket.trade.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public Vehicle createVehicle(CreateVehicleRequest request, User seller) {
        Vehicle vehicle = new Vehicle();
        vehicle.setSeller(seller);
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setBatteryCapacity(request.getBatteryCapacity());
        vehicle.setMileage(request.getMileage());
        vehicle.setCondition(request.getCondition());
        vehicle.setPrice(request.getPrice());
        vehicle.setStatus("AVAILABLE");
        vehicle.setCreatedAt(LocalDateTime.now());
        
        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesBySeller(User seller) {
        List<Vehicle> vehicles = vehicleRepository.findBySeller(seller);
        return vehicles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllAvailableVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findByStatus("AVAILABLE");
        return vehicles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        return convertToResponse(vehicle);
    }

    @Override
    public Vehicle updateVehicleStatus(Long vehicleId, String status) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }

    private VehicleResponse convertToResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getVehicleId(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getBatteryCapacity(),
                vehicle.getMileage(),
                vehicle.getCondition(),
                vehicle.getPrice(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getSeller().getFullName(),
                vehicle.getSeller().getPhone()
        );
    }
}
