package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.FavoriteListing;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.FavoriteListingRepository;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.FavoriteListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteListingServiceImpl implements FavoriteListingService {

    @Autowired
    private FavoriteListingRepository favoriteListingRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Override
    public BaseResponse<Void> addToFavorites(Long listingId, User user) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            // Check if already favorited
            if (favoriteListingRepository.existsByUserAndListing(user, listing)) {
                throw new AppException("Listing is already in your favorites");
            }

            FavoriteListing favoriteListing = new FavoriteListing(user, listing);
            favoriteListingRepository.save(favoriteListing);

            return BaseResponse.success(null, "Listing added to favorites successfully");
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Failed to add listing to favorites: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<Void> removeFromFavorites(Long listingId, User user) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            if (!favoriteListingRepository.existsByUserAndListing(user, listing)) {
                throw new AppException("Listing is not in your favorites");
            }

            favoriteListingRepository.deleteByUserAndListing(user, listing);

            return BaseResponse.success(null, "Listing removed from favorites successfully");
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Failed to remove listing from favorites: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ListingResponse>> getMyFavorites(User user) {
        try {
            List<Listing> favorites = favoriteListingRepository.findListingsByUser(user);
            List<ListingResponse> responseList = favorites.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return BaseResponse.success(responseList, "Favorites retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve favorites: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<Boolean> checkFavorite(Long listingId, User user) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Listing not found"));

            boolean isFavorite = favoriteListingRepository.existsByUserAndListing(user, listing);

            return BaseResponse.success(isFavorite, "Favorite status checked successfully");
        } catch (Exception e) {
            throw new AppException("Failed to check favorite status: " + e.getMessage());
        }
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
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

