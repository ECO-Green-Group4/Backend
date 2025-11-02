package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.FavoriteListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteListingController {

    @Autowired
    private FavoriteListingService favoriteListingService;

    @Autowired
    private AuthService authService;

    /**
     * Thêm bài viết vào danh sách yêu thích
     */
    @PostMapping("/{listingId}")
    public ResponseEntity<BaseResponse<?>> addToFavorites(
            @PathVariable Long listingId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(favoriteListingService.addToFavorites(listingId, user));
    }

    /**
     * Xóa bài viết khỏi danh sách yêu thích
     */
    @DeleteMapping("/{listingId}")
    public ResponseEntity<BaseResponse<?>> removeFromFavorites(
            @PathVariable Long listingId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(favoriteListingService.removeFromFavorites(listingId, user));
    }

    /**
     * Lấy danh sách bài viết yêu thích của người dùng
     */
    @GetMapping
    public ResponseEntity<BaseResponse<List<ListingResponse>>> getMyFavorites(
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(favoriteListingService.getMyFavorites(user));
    }

    /**
     * Kiểm tra xem bài viết có trong danh sách yêu thích không
     */
    @GetMapping("/check/{listingId}")
    public ResponseEntity<BaseResponse<Boolean>> checkFavorite(
            @PathVariable Long listingId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(favoriteListingService.checkFavorite(listingId, user));
    }
}


