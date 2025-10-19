package com.evmarket.trade.service;

import com.evmarket.trade.config.MomoConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Slf4j
public class MomoService {

    @Autowired
    private MomoConfig momoConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Tạo thanh toán với MoMo Payment Gateway (chính thức)
     * Trả về payUrl - link có QR code để user quét
     */
    public Map<String, Object> createPayment(BigDecimal amount, String orderId, String orderInfo, String returnUrl) {
        try {
            log.info("Creating MoMo Payment Gateway: orderId={}, amount={}", orderId, amount);

            // Tạo requestId duy nhất
            String requestId = orderId + "_" + System.currentTimeMillis();
            
            // Format số tiền (MoMo yêu cầu integer, không có phần thập phân)
            long amountLong = amount.longValue();

            // Tạo request body theo MoMo API
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("partnerCode", momoConfig.getPartnerCode());
            requestBody.put("partnerName", "EV Trade");
            requestBody.put("storeId", "EVTradeStore");
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amountLong);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", returnUrl != null ? returnUrl : momoConfig.getReturnUrl());
            requestBody.put("ipnUrl", momoConfig.getIpnUrl());
            requestBody.put("lang", momoConfig.getLang());
            requestBody.put("requestType", momoConfig.getRequestType()); // "captureWallet" hoặc "payWithMethod"
            requestBody.put("autoCapture", true);
            requestBody.put("extraData", ""); // Có thể thêm data bổ sung nếu cần

            // Tạo chữ ký (signature)
            String rawSignature = "accessKey=" + momoConfig.getAccessKey() +
                    "&amount=" + amountLong +
                    "&extraData=" +
                    "&ipnUrl=" + momoConfig.getIpnUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + momoConfig.getPartnerCode() +
                    "&redirectUrl=" + (returnUrl != null ? returnUrl : momoConfig.getReturnUrl()) +
                    "&requestId=" + requestId +
                    "&requestType=" + momoConfig.getRequestType();

            log.info("Raw signature: {}", rawSignature);
            String signature = signHmacSHA256(rawSignature, momoConfig.getSecretKey());
            requestBody.put("signature", signature);

            // Gửi request tới MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to MoMo API: {}", momoConfig.getPayUrl());
            log.info("Request body: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    momoConfig.getPayUrl(),
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            log.info("MoMo API response: {}", objectMapper.writeValueAsString(responseBody));

            if (responseBody == null) {
                throw new RuntimeException("MoMo API không trả về response");
            }

            // Kiểm tra response code
            Integer resultCode = (Integer) responseBody.get("resultCode");
            if (resultCode == null || resultCode != 0) {
                String message = (String) responseBody.get("message");
                throw new RuntimeException("MoMo API error: " + message);
            }

            // MoMo trả về payUrl - đây là link có QR code để user quét
            // Khi user truy cập payUrl này, MoMo sẽ hiển thị trang thanh toán với QR code
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("partnerCode", responseBody.get("partnerCode"));
            result.put("requestId", responseBody.get("requestId"));
            result.put("orderId", responseBody.get("orderId"));
            result.put("amount", responseBody.get("amount"));
            result.put("responseTime", responseBody.get("responseTime"));
            result.put("message", responseBody.get("message"));
            result.put("resultCode", responseBody.get("resultCode"));
            
            // payUrl - Link chính để user truy cập và quét QR
            result.put("payUrl", responseBody.get("payUrl"));
            
            // deeplink - Link để mở MoMo app trực tiếp (nếu có)
            result.put("deeplink", responseBody.get("deeplink"));
            
            // qrCodeUrl - Link trực tiếp đến QR code (nếu MoMo cung cấp)
            result.put("qrCodeUrl", responseBody.get("qrCodeUrl"));

            log.info("Payment created successfully. PayUrl: {}", result.get("payUrl"));
            
            return result;

        } catch (Exception e) {
            log.error("Error creating MoMo payment: ", e);
            throw new RuntimeException("Lỗi tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xác thực callback từ MoMo
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            log.info("Verifying MoMo callback: {}", params);

            String receivedSignature = params.get("signature");
            if (receivedSignature == null) {
                log.error("Signature not found in callback");
                return false;
            }

            // Tạo lại signature để so sánh
            String rawSignature = "accessKey=" + momoConfig.getAccessKey() +
                    "&amount=" + params.get("amount") +
                    "&extraData=" + params.getOrDefault("extraData", "") +
                    "&message=" + params.get("message") +
                    "&orderId=" + params.get("orderId") +
                    "&orderInfo=" + params.get("orderInfo") +
                    "&orderType=" + params.get("orderType") +
                    "&partnerCode=" + params.get("partnerCode") +
                    "&payType=" + params.get("payType") +
                    "&requestId=" + params.get("requestId") +
                    "&responseTime=" + params.get("responseTime") +
                    "&resultCode=" + params.get("resultCode") +
                    "&transId=" + params.get("transId");

            String calculatedSignature = signHmacSHA256(rawSignature, momoConfig.getSecretKey());

            boolean isValid = calculatedSignature.equals(receivedSignature);
            
            if (!isValid) {
                log.error("Invalid signature. Expected: {}, Received: {}", calculatedSignature, receivedSignature);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying MoMo callback: ", e);
            return false;
        }
    }

    private String signHmacSHA256(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac).toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo chữ ký HMAC: " + e.getMessage());
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
