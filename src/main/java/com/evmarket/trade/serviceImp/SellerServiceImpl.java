package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.VehicleResponse;
import com.evmarket.trade.response.BatteryResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SellerServiceImpl implements SellerService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private BatteryRepository batteryRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private ServicePackageRepository servicePackageRepository;

    @Autowired
    private ListingPackageRepository listingPackageRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // Vehicle management
    @Override
    public BaseResponse<VehicleResponse> createVehicle(VehicleRequest request, User seller) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setSeller(seller);
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
            VehicleResponse response = new VehicleResponse(
                    savedVehicle.getVehicleId(),
                    savedVehicle.getBrand(),
                    savedVehicle.getModel(),
                    savedVehicle.getYear(),
                    savedVehicle.getBatteryCapacity(),
                    savedVehicle.getMileage(),
                    savedVehicle.getCondition(),
                    savedVehicle.getPrice(),
                    savedVehicle.getStatus(),
                    savedVehicle.getCreatedAt(),
                    savedVehicle.getSeller().getFullName(),
                    savedVehicle.getSeller().getPhone()
            );
            return BaseResponse.success(response, "Vehicle created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create vehicle: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<VehicleResponse> updateVehicle(Long vehicleId, VehicleRequest request, User seller) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new AppException("Vehicle not found"));

            if (vehicle.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only update your own vehicles");
            }

            vehicle.setBrand(request.getBrand());
            vehicle.setModel(request.getModel());
            vehicle.setYear(request.getYear());
            vehicle.setBatteryCapacity(request.getBatteryCapacity());
            vehicle.setMileage(request.getMileage());
            vehicle.setCondition(request.getCondition());
            vehicle.setPrice(request.getPrice());

            Vehicle updatedVehicle = vehicleRepository.save(vehicle);
            VehicleResponse response = new VehicleResponse(
                    updatedVehicle.getVehicleId(),
                    updatedVehicle.getBrand(),
                    updatedVehicle.getModel(),
                    updatedVehicle.getYear(),
                    updatedVehicle.getBatteryCapacity(),
                    updatedVehicle.getMileage(),
                    updatedVehicle.getCondition(),
                    updatedVehicle.getPrice(),
                    updatedVehicle.getStatus(),
                    updatedVehicle.getCreatedAt(),
                    updatedVehicle.getSeller().getFullName(),
                    updatedVehicle.getSeller().getPhone()
            );
            return BaseResponse.success(response, "Vehicle updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to update vehicle: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> deleteVehicle(Long vehicleId, User seller) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new AppException("Vehicle not found"));

            if (vehicle.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only delete your own vehicles");
            }

            vehicleRepository.delete(vehicle);
            return BaseResponse.success(null, "Vehicle deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete vehicle: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<VehicleResponse>> getMyVehicles(User seller) {
        try {
            List<Vehicle> vehicles = vehicleRepository.findBySeller(seller);
            List<VehicleResponse> responses = vehicles.stream()
                    .map(v -> new VehicleResponse(
                            v.getVehicleId(),
                            v.getBrand(),
                            v.getModel(),
                            v.getYear(),
                            v.getBatteryCapacity(),
                            v.getMileage(),
                            v.getCondition(),
                            v.getPrice(),
                            v.getStatus(),
                            v.getCreatedAt(),
                            v.getSeller().getFullName(),
                            v.getSeller().getPhone()
                    ))
                    .toList();
            return BaseResponse.success(responses, "Vehicles retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve vehicles: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<VehicleResponse> getVehicleById(Long vehicleId, User seller) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new AppException("Vehicle not found"));

            if (vehicle.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only view your own vehicles");
            }

            VehicleResponse response = new VehicleResponse(
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
            return BaseResponse.success(response, "Vehicle retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve vehicle: " + e.getMessage());
        }
    }

    // Battery management
    @Override
    public BaseResponse<BatteryResponse> createBattery(BatteryRequest request, User seller) {
        try {
            Battery battery = new Battery();
            battery.setSeller(seller);
            battery.setType(request.getType());
            battery.setCapacity(request.getCapacity());
            battery.setHealthPercent(request.getHealthPercent());
            battery.setManufactureYear(request.getManufactureYear());
            battery.setPrice(request.getPrice());
            battery.setStatus("DRAFT");
            battery.setCreatedAt(LocalDateTime.now());

            Battery savedBattery = batteryRepository.save(battery);
            BatteryResponse response = new BatteryResponse(
                    savedBattery.getBatteryId(),
                    savedBattery.getType(),
                    savedBattery.getCapacity(),
                    savedBattery.getHealthPercent(),
                    savedBattery.getManufactureYear(),
                    savedBattery.getPrice(),
                    savedBattery.getStatus(),
                    savedBattery.getCreatedAt(),
                    savedBattery.getSeller().getFullName(),
                    savedBattery.getSeller().getPhone()
            );
            return BaseResponse.success(response, "Battery created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create battery: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<BatteryResponse> updateBattery(Long batteryId, BatteryRequest request, User seller) {
        try {
            Battery battery = batteryRepository.findById(batteryId)
                    .orElseThrow(() -> new AppException("Battery not found"));

            if (battery.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only update your own batteries");
            }

            battery.setType(request.getType());
            battery.setCapacity(request.getCapacity());
            battery.setHealthPercent(request.getHealthPercent());
            battery.setManufactureYear(request.getManufactureYear());
            battery.setPrice(request.getPrice());

            Battery updatedBattery = batteryRepository.save(battery);
            BatteryResponse response = new BatteryResponse(
                    updatedBattery.getBatteryId(),
                    updatedBattery.getType(),
                    updatedBattery.getCapacity(),
                    updatedBattery.getHealthPercent(),
                    updatedBattery.getManufactureYear(),
                    updatedBattery.getPrice(),
                    updatedBattery.getStatus(),
                    updatedBattery.getCreatedAt(),
                    updatedBattery.getSeller().getFullName(),
                    updatedBattery.getSeller().getPhone()
            );
            return BaseResponse.success(response, "Battery updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to update battery: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> deleteBattery(Long batteryId, User seller) {
        try {
            Battery battery = batteryRepository.findById(batteryId)
                    .orElseThrow(() -> new AppException("Battery not found"));

            if (battery.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only delete your own batteries");
            }

            batteryRepository.delete(battery);
            return BaseResponse.success(null, "Battery deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete battery: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<BatteryResponse>> getMyBatteries(User seller) {
        try {
            List<Battery> batteries = batteryRepository.findBySeller(seller);
            List<BatteryResponse> responses = batteries.stream()
                    .map(b -> new BatteryResponse(
                            b.getBatteryId(),
                            b.getType(),
                            b.getCapacity(),
                            b.getHealthPercent(),
                            b.getManufactureYear(),
                            b.getPrice(),
                            b.getStatus(),
                            b.getCreatedAt(),
                            b.getSeller().getFullName(),
                            b.getSeller().getPhone()
                    ))
                    .toList();
            return BaseResponse.success(responses, "Batteries retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve batteries: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<BatteryResponse> getBatteryById(Long batteryId, User seller) {
        try {
            Battery battery = batteryRepository.findById(batteryId)
                    .orElseThrow(() -> new AppException("Battery not found"));

            if (battery.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only view your own batteries");
            }

            BatteryResponse response = new BatteryResponse(
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
            return BaseResponse.success(response, "Battery retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve battery: " + e.getMessage());
        }
    }

    // Listing management - using new separate APIs
    @Override
    public BaseResponse<Listing> updateListing(Long listingId, ListingRequest request, User seller) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (listing.getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only update your own listings");
            }

            listing.setTitle(request.getTitle());
            listing.setDescription(request.getDescription());
            listing.setImages(request.getImages());
            listing.setLocation(request.getLocation());

            Listing updatedListing = listingRepository.save(listing);
            return BaseResponse.success(updatedListing, "Listing updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to update listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> deleteListing(Long listingId, User seller) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (listing.getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only delete your own listings");
            }

            listingRepository.delete(listing);
            return BaseResponse.success(null, "Listing deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<Listing>> getMyListings(User seller) {
        try {
            List<Listing> listings = listingRepository.findByUser(seller);
            return BaseResponse.success(listings, "Listings retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listings: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Listing> getListingById(Long listingId, User seller) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (listing.getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only view your own listings");
            }

            return BaseResponse.success(listing, "Listing retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listing: " + e.getMessage());
        }
    }

    // Package management
    @Override
    public BaseResponse<List<ServicePackage>> getAvailablePackages() {
        try {
            List<ServicePackage> packages = servicePackageRepository.findByStatus("ACTIVE");
            return BaseResponse.success(packages, "Available packages retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve packages: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ListingPackage> selectPackage(SelectPackageRequest request, User seller) {
        try {
            Listing listing = listingRepository.findById(request.getListingId())
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (listing.getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only select packages for your own listings");
            }

            ServicePackage servicePackage = servicePackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new AppException("Service package not found"));

            if (!"ACTIVE".equals(servicePackage.getStatus())) {
                throw new AppException("Service package is not available");
            }

            // Check if listing already has an active package
            Optional<ListingPackage> existingPackage = listingPackageRepository.findActiveByListing(listing, LocalDateTime.now());
            if (existingPackage.isPresent()) {
                throw new AppException("Listing already has an active package");
            }

            ListingPackage listingPackage = new ListingPackage();
            listingPackage.setListing(listing);
            listingPackage.setServicePackage(servicePackage);
            listingPackage.setAppliedAt(LocalDateTime.now());
            listingPackage.setExpiredAt(LocalDateTime.now().plusDays(servicePackage.getDurationDays()));
            listingPackage.setStatus("PENDING_PAYMENT");

            ListingPackage savedListingPackage = listingPackageRepository.save(listingPackage);
            return BaseResponse.success(savedListingPackage, "Package selected successfully");
        } catch (Exception e) {
            throw new AppException("Failed to select package: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<ListingPackage> getListingPackage(Long listingId, User seller) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (listing.getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only view packages for your own listings");
            }

            Optional<ListingPackage> listingPackage = listingPackageRepository.findActiveByListing(listing, LocalDateTime.now());
            if (listingPackage.isEmpty()) {
                throw new AppException("No active package found for this listing");
            }

            return BaseResponse.success(listingPackage.get(), "Listing package retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listing package: " + e.getMessage());
        }
    }

    // Payment management
    @Override
    public BaseResponse<Payment> processPackagePayment(PaymentRequest request, User seller) {
        try {
            ListingPackage listingPackage = listingPackageRepository.findById(request.getListingPackageId())
                    .orElseThrow(() -> new AppException("Listing package not found"));

            if (listingPackage.getListing().getUser().getUserId() != seller.getUserId()) {
                throw new AppException("You can only pay for your own listing packages");
            }

            if (!"PENDING_PAYMENT".equals(listingPackage.getStatus())) {
                throw new AppException("This listing package is not pending payment");
            }

            // Validate payment amount
            if (request.getAmount().compareTo(listingPackage.getServicePackage().getListingFee()) != 0) {
                throw new AppException("Payment amount does not match package fee");
            }

            // SỬA LẠI: Sử dụng Payment entity mới với constructor đúng
            Payment payment = new Payment();
            payment.setPaymentType(Payment.PaymentType.PACKAGE);
            payment.setListingPackageId(listingPackage.getListingPackageId());
            payment.setPayer(seller);
            payment.setPaymentGateway("VNPAY"); // Hoặc request.getPaymentMethod() nếu có
            payment.setAmount(request.getAmount());
            payment.setCurrency("VND");
            payment.setPaymentStatus("SUCCESS");
            payment.setPaymentDate(LocalDateTime.now());
            payment.setCreatedAt(LocalDateTime.now());

            Payment savedPayment = paymentRepository.save(payment);

            // Update listing package status
            listingPackage.setStatus("ACTIVE");
            listingPackageRepository.save(listingPackage);

            // Update listing status
            listingPackage.getListing().setStatus("ACTIVE");
            listingRepository.save(listingPackage.getListing());

            return BaseResponse.success(savedPayment, "Payment processed successfully");

        } catch (Exception e) {
            throw new AppException("Failed to process payment: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<Payment>> getMyPayments(User seller) {
        try {
            List<Payment> payments = paymentRepository.findByPayer(seller);
            return BaseResponse.success(payments, "Payments retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve payments: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Payment> getPaymentById(Long paymentId, User seller) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new AppException("Payment not found"));

            if (payment.getPayer().getUserId() != seller.getUserId()) {
                throw new AppException("You can only view your own payments");
            }

            return BaseResponse.success(payment, "Payment retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve payment: " + e.getMessage());
        }
    }

    // Order management
    @Override
    public BaseResponse<Void> confirmOrder(Long orderId, User seller) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Order not found"));

            if (order.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only confirm orders for your own listings");
            }

            if (!"PENDING".equals(order.getStatus())) {
                throw new AppException("Only pending orders can be confirmed");
            }

            order.setStatus("CONFIRMED");
            orderRepository.save(order);

            return BaseResponse.success(null, "Order confirmed successfully");
        } catch (Exception e) {
            throw new AppException("Failed to confirm order: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> rejectOrder(Long orderId, User seller) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Order not found"));

            if (order.getSeller().getUserId() != seller.getUserId()) {
                throw new AppException("You can only reject orders for your own listings");
            }

            if (!"PENDING".equals(order.getStatus())) {
                throw new AppException("Only pending orders can be rejected");
            }

            order.setStatus("REJECTED");
            orderRepository.save(order);

            return BaseResponse.success(null, "Order rejected successfully");
        } catch (Exception e) {
            throw new AppException("Failed to reject order: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<List<OrderResponse>> getOrdersForMyListings(User seller) {
        try {
            List<Order> orders = orderRepository.findBySeller(seller);
            List<OrderResponse> responses = orders.stream()
                    .map(this::convertToOrderResponse)
                    .toList();
            return BaseResponse.success(responses, "Orders retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve orders: " + e.getMessage());
        }
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .listingId(order.getListing() != null ? order.getListing().getListingId() : null)
                .buyer(convertUserToUserInfo(order.getBuyer()))
                .seller(convertUserToUserInfo(order.getSeller()))
                .basePrice(order.getBasePrice())
                .commissionFee(order.getCommissionFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .assignedStaffId(order.getAssignedStaffId())
                .build();
    }

    private UserInfoResponse convertUserToUserInfo(User user) {
        if (user == null) return null;
        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                // Hide phone, identityCard, address in seller order views
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .build();
    }
}