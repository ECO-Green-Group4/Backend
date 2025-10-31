package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.ListingPackage;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.ServicePackage;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.repository.ListingPackageRepository;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.repository.ServicePackageRepository;
import com.evmarket.trade.request.SelectPackageRequest;
import com.evmarket.trade.response.ServicePackageResponse;
import com.evmarket.trade.service.ServicePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicePackageServiceImpl implements ServicePackageService {

    @Autowired
    private ServicePackageRepository servicePackageRepository;
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private ListingPackageRepository listingPackageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServicePackageResponse> getAllActivePackages() {
        List<ServicePackage> packages = servicePackageRepository.findByStatus("ACTIVE");
        return packages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServicePackageResponse getPackageById(Long packageId) {
        ServicePackage servicePackage = servicePackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Service package not found with id: " + packageId));
        return convertToResponse(servicePackage);
    }

    @Override
    public void applyPackageToListing(SelectPackageRequest request, User user) {
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + request.getListingId()));
        
        // Check if listing belongs to user
        if (listing.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("Listing does not belong to user");
        }
        
        ServicePackage servicePackage = servicePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Service package not found with id: " + request.getPackageId()));
        
        // Check if package is active
        if (!"ACTIVE".equals(servicePackage.getStatus())) {
            throw new RuntimeException("Service package is not active");
        }
        
        // Create listing package relationship
        ListingPackage listingPackage = new ListingPackage();
        listingPackage.setListing(listing);
        listingPackage.setServicePackage(servicePackage);
        listingPackage.setAppliedAt(LocalDateTime.now());
        listingPackage.setExpiredAt(LocalDateTime.now().plusDays(servicePackage.getDurationDays()));
        listingPackage.setStatus("ACTIVE");
        
        listingPackageRepository.save(listingPackage);
    }

    private ServicePackageResponse convertToResponse(ServicePackage servicePackage) {
        return new ServicePackageResponse(
                servicePackage.getPackageId(),
                servicePackage.getName(),
                servicePackage.getListingLimit(),
                servicePackage.getListingFee(),
                servicePackage.getHighlight(),
                servicePackage.getDurationDays(),
                servicePackage.getCommissionDiscount(),
                servicePackage.getStatus()
        );
    }
}
