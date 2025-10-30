package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Vehicle;
import com.evmarket.trade.entity.Battery;
import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.repository.VehicleRepository;
import com.evmarket.trade.repository.BatteryRepository;
import com.evmarket.trade.repository.ListingPackageRepository;
import com.evmarket.trade.repository.ServicePackageRepository;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.request.CreateVehicleListingRequest;
import com.evmarket.trade.request.CreateBatteryListingRequest;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    
    @Autowired
    private ListingPackageRepository listingPackageRepository;
    
    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Override
    public ListingResponse createVehicleListing(CreateVehicleListingRequest request, User user) {
        // Validate listing package exists and is active
        ServicePackage servicePackage = servicePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException("Service package not found"));
        
        if (!"ACTIVE".equals(servicePackage.getStatus())) {
            throw new AppException("Selected service package is not active");
        }
        
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
                .postType(servicePackage.getName()) // Set post type from package name
                .packageQuantity(request.getQuantity()) // Set package quantity
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
        
        // Create ListingPackage to link listing with service package
        ListingPackage listingPackage = new ListingPackage();
        listingPackage.setListing(saved);
        listingPackage.setServicePackage(servicePackage);
        listingPackage.setUser(user);
        listingPackage.setQuantity(request.getQuantity()); // Set quantity
        listingPackage.setAppliedAt(LocalDateTime.now());
        // Calculate expiration date: duration days * quantity
        listingPackage.setExpiredAt(LocalDateTime.now().plusDays(servicePackage.getDurationDays() * request.getQuantity()));
        listingPackage.setStatus("PENDING_PAYMENT");
        ListingPackage savedListingPackage = listingPackageRepository.save(listingPackage);
        
        // Convert to response and include listing package info
        ListingResponse response = convertToResponse(saved);
        response.setListingPackageId(savedListingPackage.getListingPackageId());
        // Calculate total amount: listing fee * quantity
        response.setPackageAmount(savedListingPackage.getServicePackage().getListingFee().multiply(BigDecimal.valueOf(request.getQuantity())));
        response.setPackageStatus(savedListingPackage.getStatus());
        response.setPackageExpiredAt(savedListingPackage.getExpiredAt());
        
        return response;
    }

    @Override
    public ListingResponse createBatteryListing(CreateBatteryListingRequest request, User user) {
        // Validate listing package exists and is active
        ServicePackage servicePackage = servicePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new AppException("Service package not found"));
        
        if (!"ACTIVE".equals(servicePackage.getStatus())) {
            throw new AppException("Selected service package is not active");
        }
        
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
                .postType(servicePackage.getName()) // Set post type from package name
                .packageQuantity(request.getQuantity()) // Set package quantity
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
        
        // Create ListingPackage to link listing with service package
        ListingPackage listingPackage = new ListingPackage();
        listingPackage.setListing(saved);
        listingPackage.setServicePackage(servicePackage);
        listingPackage.setUser(user);
        listingPackage.setQuantity(request.getQuantity()); // Set quantity
        listingPackage.setAppliedAt(LocalDateTime.now());
        // Calculate expiration date: duration days * quantity
        listingPackage.setExpiredAt(LocalDateTime.now().plusDays(servicePackage.getDurationDays() * request.getQuantity()));
        listingPackage.setStatus("PENDING_PAYMENT");
        ListingPackage savedListingPackage = listingPackageRepository.save(listingPackage);
        
        // Convert to response and include listing package info
        ListingResponse response = convertToResponse(saved);
        response.setListingPackageId(savedListingPackage.getListingPackageId());
        // Calculate total amount: listing fee * quantity
        response.setPackageAmount(savedListingPackage.getServicePackage().getListingFee().multiply(BigDecimal.valueOf(request.getQuantity())));
        response.setPackageStatus(savedListingPackage.getStatus());
        response.setPackageExpiredAt(savedListingPackage.getExpiredAt());
        
        return response;
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
    public ListingResponse updateListingStatus(Long listingId, String status) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));
        listing.setStatus(status);
        Listing saved = listingRepository.save(listing);
        return convertToResponse(saved);
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
        response.setPostType(listing.getPostType());
        
        // Set vehicle specific fields
        if ("vehicle".equals(listing.getItemType())) {
            response.setBrand(listing.getBrand());
            response.setModel(listing.getModel());
            response.setYear(listing.getYear());
            response.setBatteryCapacity(listing.getBatteryCapacity());
            response.setMileage(listing.getMileage());
            response.setCondition(listing.getCondition());
            response.setBodyType(listing.getBodyType());
            response.setColor(listing.getColor());
            response.setInspection(listing.getInspection());
            response.setOrigin(listing.getOrigin());
            response.setNumberOfSeats(listing.getNumberOfSeats());
            response.setLicensePlate(listing.getLicensePlate());
            response.setAccessories(listing.getAccessories());
        }
        
        // Set battery specific fields
        if ("battery".equals(listing.getItemType())) {
            response.setBatteryBrand(listing.getBatteryBrand());
            response.setVoltage(listing.getVoltage());
            response.setType(listing.getType());
            response.setCapacity(listing.getCapacity());
            response.setHealthPercent(listing.getHealthPercent());
            response.setManufactureYear(listing.getManufactureYear());
            response.setChargeCycles(listing.getChargeCycles());
            response.setOrigin(listing.getOrigin());
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