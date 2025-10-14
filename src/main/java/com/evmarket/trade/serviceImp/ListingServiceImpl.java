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
import com.evmarket.trade.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public Listing createVehicleListing(CreateVehicleListingRequest request, User user) {
        // Tạo Vehicle trước
        Vehicle vehicle = new Vehicle();
        vehicle.setSeller(user);
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setBatteryCapacity(request.getBatteryCapacity());
        vehicle.setMileage(request.getMileage());
        vehicle.setCondition(request.getCondition());
        vehicle.setPrice(request.getPrice());
        vehicle.setStatus("DRAFT");
        vehicle.setCreatedAt(LocalDateTime.now());
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        
        // Tạo Listing và reference đến Vehicle
        Listing listing = new Listing();
        listing.setUser(user);
        listing.setItemType("vehicle");
        listing.setItemId(savedVehicle.getVehicleId()); // Reference đến Vehicle
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setImages(request.getImages());
        listing.setLocation(request.getLocation());
        listing.setPrice(request.getPrice());
        listing.setStatus("DRAFT");
        listing.setCreatedAt(LocalDateTime.now());
        
        // Set vehicle specific fields trong listing
        listing.setBrand(request.getBrand());
        listing.setModel(request.getModel());
        listing.setYear(request.getYear());
        listing.setBatteryCapacity(request.getBatteryCapacity());
        listing.setMileage(request.getMileage());
        listing.setCondition(request.getCondition());
        
        return listingRepository.save(listing);
    }

    @Override
    public Listing createBatteryListing(CreateBatteryListingRequest request, User user) {
        // Tạo Battery trước
        Battery battery = new Battery();
        battery.setSeller(user);
        battery.setType(request.getType());
        battery.setCapacity(request.getCapacity());
        battery.setHealthPercent(request.getHealthPercent());
        battery.setManufactureYear(request.getManufactureYear());
        battery.setPrice(request.getPrice());
        battery.setStatus("DRAFT");
        battery.setCreatedAt(LocalDateTime.now());
        
        Battery savedBattery = batteryRepository.save(battery);
        
        // Tạo Listing và reference đến Battery
        Listing listing = new Listing();
        listing.setUser(user);
        listing.setItemType("battery");
        listing.setItemId(savedBattery.getBatteryId()); // Reference đến Battery
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setImages(request.getImages());
        listing.setLocation(request.getLocation());
        listing.setPrice(request.getPrice());
        listing.setStatus("DRAFT");
        listing.setCreatedAt(LocalDateTime.now());
        
        // Set battery specific fields trong listing
        listing.setType(request.getType());
        listing.setCapacity(request.getCapacity());
        listing.setHealthPercent(request.getHealthPercent());
        listing.setManufactureYear(request.getManufactureYear());
        
        return listingRepository.save(listing);
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
        response.setUser(listing.getUser());
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
}