package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Vehicle;
import com.evmarket.trade.entity.Battery;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.repository.VehicleRepository;
import com.evmarket.trade.repository.BatteryRepository;
import com.evmarket.trade.request.CreateVehicleListingRequest;
import com.evmarket.trade.request.CreateBatteryListingRequest;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListingServiceImpl implements ListingService {

    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private BatteryRepository batteryRepository;

    @Override
    public ListingResponse createVehicleListing(CreateVehicleListingRequest request, User user) {
        // Tạo Vehicle trước với tất cả các trường từ Figma form
        Vehicle vehicle = Vehicle.builder()
                .seller(user)
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .bodyType(request.getBodyType())
                .color(request.getColor())
                .mileage(request.getMileage())
                .inspection(request.getInspection())
                .origin(request.getOrigin())
                .numberOfSeats(request.getNumberOfSeats())
                .licensePlate(request.getLicensePlate())
                .accessories(request.getAccessories())
                .batteryCapacity(request.getBatteryCapacity())
                .condition(request.getCondition())
                .price(request.getPrice())
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .build();
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Tạo Listing và reference đến Vehicle
        Listing listing = Listing.builder()
                .user(user)
                .itemType("vehicle")
                .itemId(savedVehicle.getVehicleId())
                .title(request.getTitle())
                .description(request.getDescription())
                .images(request.getImages())
                .location(request.getLocation())
                .price(request.getPrice())
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                // Vehicle specific fields trong listing
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .bodyType(request.getBodyType())
                .color(request.getColor())
                .mileage(request.getMileage())
                .inspection(request.getInspection())
                .origin(request.getOrigin())
                .numberOfSeats(request.getNumberOfSeats())
                .licensePlate(request.getLicensePlate())
                .accessories(request.getAccessories())
                .batteryCapacity(request.getBatteryCapacity())
                .condition(request.getCondition())
                .build();
        
        Listing saved = listingRepository.save(listing);
        return convertToResponse(saved);
    }

    @Override
    public ListingResponse createBatteryListing(CreateBatteryListingRequest request, User user) {
        // Tạo Battery trước với tất cả các trường từ Figma form
        Battery battery = Battery.builder()
                .seller(user)
                .brand(request.getBrand())
                .type(request.getType())
                .capacity(request.getCapacity())
                .healthPercent(request.getHealthPercent())
                .manufactureYear(request.getManufactureYear())
                .voltage(request.getVoltage())
                .chargeCycles(request.getChargeCycles())
                .origin(request.getOrigin())
                .price(request.getPrice())
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                .build();
        
        Battery savedBattery = batteryRepository.save(battery);
        
        // Tạo Listing và reference đến Battery
        Listing listing = Listing.builder()
                .user(user)
                .itemType("battery")
                .itemId(savedBattery.getBatteryId())
                .title(request.getTitle())
                .description(request.getDescription())
                .images(request.getImages())
                .location(request.getLocation())
                .price(request.getPrice())
                .status("DRAFT")
                .createdAt(LocalDateTime.now())
                // Battery specific fields trong listing
                .batteryBrand(request.getBrand())
                .type(request.getType())
                .capacity(request.getCapacity())
                .healthPercent(request.getHealthPercent())
                .manufactureYear(request.getManufactureYear())
                .voltage(request.getVoltage())
                .chargeCycles(request.getChargeCycles())
                .origin(request.getOrigin())
                .build();
        
        Listing saved = listingRepository.save(listing);
        return convertToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByUser(User user) {
        List<Listing> listings = listingRepository.findByUser(user);
        return listings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getAllAvailableListings() {
        List<Listing> listings = listingRepository.findByStatus("ACTIVE");
        return listings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getAllListings() {
        List<Listing> listings = listingRepository.findAll();
        return listings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByItemType(String itemType) {
        List<Listing> listings = listingRepository.findActiveByItemType(itemType);
        return listings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ListingResponse getListingById(Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        return convertToResponse(listing);
    }

    @Override
    public Listing updateListingStatus(Long listingId, String status) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        listing.setStatus(status);
        return listingRepository.save(listing);
    }

    private ListingResponse convertToResponse(Listing listing) {
        ListingResponse response = new ListingResponse();
        response.setListingId(listing.getListingId());
        response.setUser(convertUserToUserInfoResponse(listing.getUser()));
        response.setItemType(listing.getItemType());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());
        response.setImages(listing.getImages());
        response.setLocation(listing.getLocation());
        response.setPrice(listing.getPrice());
        response.setStatus(listing.getStatus());
        response.setCreatedAt(listing.getCreatedAt());
        
        // Set vehicle specific fields
        if ("vehicle".equals(listing.getItemType())) {
            response.setBrand(listing.getBrand());
            response.setModel(listing.getModel());
            response.setYear(listing.getYear());
            response.setBatteryCapacity(listing.getBatteryCapacity());
            response.setMileage(listing.getMileage());
            response.setCondition(listing.getCondition());
        }
        
        // Set battery specific fields
        if ("battery".equals(listing.getItemType())) {
            response.setType(listing.getType());
            response.setCapacity(listing.getCapacity());
            response.setHealthPercent(listing.getHealthPercent());
            response.setManufactureYear(listing.getManufactureYear());
        }
        
        return response;
    }
    
    private UserInfoResponse convertUserToUserInfoResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                // Hide phone in listing responses; expose only non-sensitive fields
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                // Do NOT expose sensitive fields like identity card, password, address
                .createdAt(user.getCreatedAt())
                .build();
    }
}