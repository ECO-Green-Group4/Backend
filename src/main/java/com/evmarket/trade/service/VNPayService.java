package com.evmarket.trade.service;

import com.evmarket.trade.config.VNPayConfig;
import com.evmarket.trade.util.VNPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    /**
     * Create VNPay payment URL
     * @param amount Payment amount
     * @param orderInfo Order information
     * @param txnRef Transaction reference code (unique)
     * @param ipAddress Customer's IP address
     * @param bankCode Bank code (optional)
     * @return Payment URL to redirect user
     */
    public String createPaymentUrl(BigDecimal amount, String orderInfo, String txnRef, 
                                   String ipAddress, String bankCode) {
        try {
            log.info("Creating VNPay payment: txnRef={}, amount={}", txnRef, amount);

            // VNPay requires amount in VND * 100 (no decimal places)
            long amountInVND = amount.multiply(BigDecimal.valueOf(100)).longValue();

            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnPayConfig.getVnpVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getVnpCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getVnpTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(amountInVND));
            vnpParams.put("vnp_CurrCode", vnPayConfig.getVnpCurrencyCode());
            
            if (bankCode != null && !bankCode.isEmpty()) {
                vnpParams.put("vnp_BankCode", bankCode);
            }
            
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", vnPayConfig.getVnpOrderType());
            vnpParams.put("vnp_Locale", vnPayConfig.getVnpLocale());
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
            vnpParams.put("vnp_IpAddr", ipAddress);

            // Transaction creation time (yyyyMMddHHmmss)
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            // Expiry time (15 minutes)
            cld.add(Calendar.MINUTE, 15);
            String vnpExpireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);

            // Sort parameters alphabetically
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            // Create hash data
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnpParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            
            String queryUrl = query.toString();
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            String paymentUrl = vnPayConfig.getVnpPayUrl() + "?" + queryUrl;
            
            log.info("VNPay payment URL created successfully: {}", paymentUrl);
            return paymentUrl;
            
        } catch (Exception e) {
            log.error("Error creating VNPay payment URL: ", e);
            throw new RuntimeException("Error creating VNPay payment URL: " + e.getMessage());
        }
    }

    /**
     * Create payment with simple information
     */
    public String createPayment(BigDecimal amount, String orderId, String orderInfo, 
                               String ipAddress) {
        return createPaymentUrl(amount, orderInfo, orderId, ipAddress, null);
    }

    /**
     * Verify signature from VNPay callback
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            log.info("Verifying VNPay callback");
            
            String vnpSecureHash = params.get("vnp_SecureHash");
            if (vnpSecureHash == null) {
                log.error("vnp_SecureHash not found in callback");
                return false;
            }
            
            // Remove unnecessary parameters
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");
            
            // Sort parameters alphabetically
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            
            // Create hash data
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            
            // Calculate secure hash
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getVnpHashSecret(), hashData.toString());
            
            boolean isValid = calculatedHash.equals(vnpSecureHash);
            
            if (!isValid) {
                log.error("Invalid VNPay signature. Expected: {}, Received: {}", 
                         calculatedHash, vnpSecureHash);
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("Error verifying VNPay callback: ", e);
            return false;
        }
    }

    /**
     * Get IP address from request
     */
    public String getIpAddress(HttpServletRequest request) {
        return VNPayUtil.getIpAddress(request);
    }
}

