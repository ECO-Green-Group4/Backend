package com.evmarket.trade.service;

import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.request.VNPayCallbackRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class VNPayService {

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String vnpUrl;

    @Value("${vnpay.version}")
    private String vnpVersion;

    @Value("${vnpay.command}")
    private String vnpCommand;

    @Value("${vnpay.curr-code}")
    private String vnpCurrCode;

    @Value("${vnpay.locale}")
    private String vnpLocale;

    /**
     * Tạo URL thanh toán VNPay
     */
    public String createPayment(BigDecimal amount, String orderId, String orderInfo, String returnUrl) {
        try {
            log.info("Tạo payment VNPay: amount={}, orderId={}, orderInfo={}", amount, orderId, orderInfo);

            // Chuẩn bị parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnpVersion);
            vnpParams.put("vnp_Command", vnpCommand);
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue()));
            vnpParams.put("vnp_CurrCode", vnpCurrCode);
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_Locale", vnpLocale);
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", "127.0.0.1"); // Trong production lấy từ request
            vnpParams.put("vnp_CreateDate", getCurrentTimeString());
            vnpParams.put("vnp_OrderType", "other");

            // Tạo checksum
            String checksum = createChecksum(vnpParams);
            vnpParams.put("vnp_SecureHash", checksum);

            // Build payment URL
            String paymentUrl = buildPaymentUrl(vnpParams);

            log.info("Tạo URL thanh toán VNPay thành công: {}", paymentUrl);
            return paymentUrl;

        } catch (Exception e) {
            log.error("Lỗi tạo URL thanh toán VNPay: ", e);
            throw new AppException("Lỗi tạo URL thanh toán VNPay: " + e.getMessage());
        }
    }

    /**
     * Xác thực callback từ VNPay
     */
    public boolean verifyReturn(VNPayCallbackRequest request) {
        try {
            log.info("Xác thực VNPay callback: vnp_TxnRef={}, vnp_ResponseCode={}",
                    request.getVnp_TxnRef(), request.getVnp_ResponseCode());

            // Chuẩn bị parameters để verify - chỉ lấy các param cần thiết
            Map<String, String> verifyParams = new HashMap<>();
            if (request.getVnp_Version() != null) verifyParams.put("vnp_Version", request.getVnp_Version());
            if (request.getVnp_Command() != null) verifyParams.put("vnp_Command", request.getVnp_Command());
            if (request.getVnp_TmnCode() != null) verifyParams.put("vnp_TmnCode", request.getVnp_TmnCode());
            if (request.getVnp_Amount() != null) verifyParams.put("vnp_Amount", request.getVnp_Amount());
            if (request.getVnp_CurrCode() != null) verifyParams.put("vnp_CurrCode", request.getVnp_CurrCode());
            if (request.getVnp_TxnRef() != null) verifyParams.put("vnp_TxnRef", request.getVnp_TxnRef());
            if (request.getVnp_OrderInfo() != null) verifyParams.put("vnp_OrderInfo", request.getVnp_OrderInfo());
            if (request.getVnp_Locale() != null) verifyParams.put("vnp_Locale", request.getVnp_Locale());
            if (request.getVnp_ReturnUrl() != null) verifyParams.put("vnp_ReturnUrl", request.getVnp_ReturnUrl());
            if (request.getVnp_IpAddr() != null) verifyParams.put("vnp_IpAddr", request.getVnp_IpAddr());
            if (request.getVnp_CreateDate() != null) verifyParams.put("vnp_CreateDate", request.getVnp_CreateDate());
            if (request.getVnp_OrderType() != null) verifyParams.put("vnp_OrderType", request.getVnp_OrderType());
            if (request.getVnp_BankCode() != null) verifyParams.put("vnp_BankCode", request.getVnp_BankCode());
            if (request.getVnp_BankTranNo() != null) verifyParams.put("vnp_BankTranNo", request.getVnp_BankTranNo());
            if (request.getVnp_CardType() != null) verifyParams.put("vnp_CardType", request.getVnp_CardType());
            if (request.getVnp_PayDate() != null) verifyParams.put("vnp_PayDate", request.getVnp_PayDate());
            if (request.getVnp_ResponseCode() != null) verifyParams.put("vnp_ResponseCode", request.getVnp_ResponseCode());
            if (request.getVnp_TransactionNo() != null) verifyParams.put("vnp_TransactionNo", request.getVnp_TransactionNo());
            if (request.getVnp_TransactionStatus() != null) verifyParams.put("vnp_TransactionStatus", request.getVnp_TransactionStatus());
            if (request.getVnp_SecureHashType() != null) verifyParams.put("vnp_SecureHashType", request.getVnp_SecureHashType());

            // Loại bỏ các param null hoặc empty
            verifyParams.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isEmpty());

            // Tạo checksum để so sánh
            String calculatedChecksum = createChecksum(verifyParams);
            String receivedChecksum = request.getVnp_SecureHash();

            boolean isValid = calculatedChecksum.equals(receivedChecksum);

            if (isValid) {
                log.info("Xác thực VNPay callback thành công: vnp_TxnRef={}", request.getVnp_TxnRef());
            } else {
                log.error("Xác thực VNPay callback thất bại: calculated={}, received={}",
                        calculatedChecksum, receivedChecksum);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Lỗi xác thực VNPay callback: ", e);
            return false;
        }
    }

    /**
     * Tạo checksum theo chuẩn VNPay
     */
    private String createChecksum(Map<String, String> params) {
        try {
            // Sắp xếp parameters theo thứ tự alphabet
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append("=").append(fieldValue).append("&");
                }
            }

            // Remove ký tự '&' cuối cùng
            if (hashData.length() > 0) {
                hashData.setLength(hashData.length() - 1);
            }

            // Mã hóa SHA256
            return hmacSHA512(hashSecret, hashData.toString());

        } catch (Exception e) {
            log.error("Lỗi tạo checksum VNPay: ", e);
            throw new AppException("Lỗi tạo checksum VNPay");
        }
    }

    /**
     * HMAC SHA512 encoding
     */
    private String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }

            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            log.error("Lỗi HMAC SHA512: ", ex);
            return "";
        }
    }

    /**
     * Build payment URL từ parameters
     */
    private String buildPaymentUrl(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(vnpUrl);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.queryParam(entry.getKey(),
                    URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return builder.build().toUriString();
    }

    /**
     * Lấy thời gian hiện tại theo format yyyyMMddHHmmss
     */
    private String getCurrentTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    /**
     * Kiểm tra response code từ VNPay
     */
    public boolean isSuccessResponse(String responseCode) {
        return "00".equals(responseCode);
    }

    /**
     * Lấy thông báo lỗi từ response code
     */
    public String getResponseMessage(String responseCode) {
        switch (responseCode) {
            case "00": return "Giao dịch thành công";
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10": return "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11": return "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12": return "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13": return "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24": return "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51": return "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
            case "65": return "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75": return "Ngân hàng thanh toán đang bảo trì.";
            case "79": return "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "99": return "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default: return "Lỗi không xác định";
        }
    }
}