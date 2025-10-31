package com.evmarket.trade.service;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ListingResponse;

import java.util.List;

public interface FavoriteListingService {
    
    BaseResponse<Void> addToFavorites(Long listingId, User user);
    
    BaseResponse<Void> removeFromFavorites(Long listingId, User user);
    
    BaseResponse<List<ListingResponse>> getMyFavorites(User user);
    
    BaseResponse<Boolean> checkFavorite(Long listingId, User user);
}

