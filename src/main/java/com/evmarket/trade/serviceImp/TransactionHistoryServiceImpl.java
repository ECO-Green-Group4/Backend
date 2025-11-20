package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.response.TransactionHistoryResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<TransactionHistoryResponse>> getMyTransactionHistory(User user) {
        try {
            // Get all orders where user is buyer or seller
            List<Order> ordersAsBuyer = orderRepository.findByBuyer(user);
            List<Order> ordersAsSeller = orderRepository.findBySeller(user);
            
            // Combine and remove duplicates
            List<Order> allOrders = new ArrayList<>();
            allOrders.addAll(ordersAsBuyer);
            allOrders.addAll(ordersAsSeller);
            
            // Remove duplicates by orderId
            allOrders = allOrders.stream()
                    .collect(Collectors.toMap(
                            Order::getOrderId,
                            order -> order,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Sort by order date descending
            allOrders.sort((o1, o2) -> {
                if (o1.getOrderDate() == null && o2.getOrderDate() == null) return 0;
                if (o1.getOrderDate() == null) return 1;
                if (o2.getOrderDate() == null) return -1;
                return o2.getOrderDate().compareTo(o1.getOrderDate());
            });

            List<TransactionHistoryResponse> responses = allOrders.stream()
                    .map(order -> convertToTransactionHistory(order))
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Lịch sử giao dịch đã được lấy thành công");

        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy lịch sử giao dịch: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<TransactionHistoryResponse>> getMyTransactionHistory(
            User user, 
            String status, 
            LocalDateTime fromDate, 
            LocalDateTime toDate) {
        try {
            // Get all orders where user is buyer or seller
            List<Order> ordersAsBuyer = orderRepository.findByBuyer(user);
            List<Order> ordersAsSeller = orderRepository.findBySeller(user);
            
            // Combine and remove duplicates
            List<Order> allOrders = new ArrayList<>();
            allOrders.addAll(ordersAsBuyer);
            allOrders.addAll(ordersAsSeller);
            
            // Remove duplicates
            allOrders = allOrders.stream()
                    .collect(Collectors.toMap(
                            Order::getOrderId,
                            order -> order,
                            (existing, replacement) -> existing
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Apply filters
            List<Order> filteredOrders = allOrders.stream()
                    .filter(order -> {
                        // Filter by status
                        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("ALL")) {
                            if (!status.equalsIgnoreCase(order.getStatus())) {
                                return false;
                            }
                        }
                        
                        // Filter by date range
                        if (fromDate != null && order.getOrderDate() != null) {
                            if (order.getOrderDate().isBefore(fromDate)) {
                                return false;
                            }
                        }
                        
                        if (toDate != null && order.getOrderDate() != null) {
                            if (order.getOrderDate().isAfter(toDate)) {
                                return false;
                            }
                        }
                        
                        return true;
                    })
                    .collect(Collectors.toList());

            // Sort by order date descending
            filteredOrders.sort((o1, o2) -> {
                if (o1.getOrderDate() == null && o2.getOrderDate() == null) return 0;
                if (o1.getOrderDate() == null) return 1;
                if (o2.getOrderDate() == null) return -1;
                return o2.getOrderDate().compareTo(o1.getOrderDate());
            });

            List<TransactionHistoryResponse> responses = filteredOrders.stream()
                    .map(order -> convertToTransactionHistory(order))
                    .collect(Collectors.toList());

            return BaseResponse.success(responses, "Lịch sử giao dịch đã được lấy thành công");

        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy lịch sử giao dịch: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<TransactionHistoryResponse> getTransactionDetail(Long orderId, User user) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy đơn hàng với ID: " + orderId));

            // Check if user is buyer or seller
            boolean isBuyer = order.getBuyer().getUserId() == user.getUserId();
            boolean isSeller = order.getSeller().getUserId() == user.getUserId();
            
            if (!isBuyer && !isSeller) {
                throw new AppException("Bạn không có quyền xem chi tiết giao dịch này");
            }

            TransactionHistoryResponse response = convertToTransactionHistory(order);
            return BaseResponse.success(response, "Chi tiết giao dịch đã được lấy thành công");

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy chi tiết giao dịch: " + e.getMessage());
        }
    }

    private TransactionHistoryResponse convertToTransactionHistory(Order order) {
        // Get listing information
        Listing listing = order.getListing();
        String listingTitle = listing != null ? listing.getTitle() : "N/A";
        String itemType = listing != null ? listing.getItemType() : "N/A";

        // Get payment information (find payment by contractId)
        TransactionHistoryResponse.PaymentInfo paymentInfo = null;
        Contract contract = contractRepository.findByOrder(order);
        if (contract != null) {
            List<Payment> payments = paymentRepository.findByContractId(contract.getContractId());
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0); // Get the first payment
                paymentInfo = TransactionHistoryResponse.PaymentInfo.builder()
                        .paymentId(payment.getPaymentId())
                        .paymentStatus(payment.getPaymentStatus())
                        .paymentGateway(payment.getPaymentGateway())
                        .amount(payment.getAmount())
                        .paymentDate(payment.getPaymentDate())
                        .paymentType(payment.getPaymentType() != null ? payment.getPaymentType().name() : null)
                        .build();
            }
        }

        // Get contract information
        TransactionHistoryResponse.ContractInfo contractInfo = null;
        if (contract != null) {
            contractInfo = TransactionHistoryResponse.ContractInfo.builder()
                    .contractId(contract.getContractId())
                    .contractStatus(contract.getContractStatus())
                    .sellerSigned(contract.getSignedBySeller())
                    .buyerSigned(contract.getSignedByBuyer())
                    .signedAt(contract.getSignedAt())
                    .build();
        }

        // Get review information
        List<Review> reviews = reviewRepository.findByOrder(order);
        List<TransactionHistoryResponse.ReviewInfo> reviewInfos = reviews.stream()
                .map(review -> TransactionHistoryResponse.ReviewInfo.builder()
                        .reviewId(review.getReviewId())
                        .reviewer(convertUserToInfo(review.getReviewer()))
                        .targetUser(convertUserToInfo(review.getTargetUser()))
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return TransactionHistoryResponse.builder()
                .orderId(order.getOrderId())
                .listingId(listing != null ? listing.getListingId() : null)
                .listingTitle(listingTitle)
                .itemType(itemType)
                .buyer(convertUserToInfo(order.getBuyer()))
                .seller(convertUserToInfo(order.getSeller()))
                .orderStatus(order.getStatus())
                .orderDate(order.getOrderDate())
                .basePrice(order.getBasePrice())
                .commissionFee(order.getCommissionFee())
                .totalAmount(order.getTotalAmount())
                .payment(paymentInfo)
                .contract(contractInfo)
                .reviews(reviewInfos)
                .build();
    }

    private UserInfoResponse convertUserToInfo(User user) {
        if (user == null) return null;
        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

