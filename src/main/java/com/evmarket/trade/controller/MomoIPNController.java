package com.evmarket.trade.controller;

import com.evmarket.trade.request.MomoCallbackRequest;
import com.evmarket.trade.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class MomoIPNController {

    @Autowired
    private PaymentService paymentService;

    // IPN (Instant Payment Notification) - MoMo gọi server-to-server
    // ĐỔI ENDPOINT THÀNH /momo-ipn (thay vì /momo-callback)
    @PostMapping("/momo-ipn")
    public ResponseEntity<String> handleMoMoIPN(@RequestBody Map<String, String> params) {
        try {
            log.info("Received MoMo IPN: {}", params);

            // Convert params to MoMoCallbackRequest
            MomoCallbackRequest request = new MomoCallbackRequest();
            request.setPartnerCode(params.get("partnerCode"));
            request.setOrderId(params.get("orderId"));
            request.setRequestId(params.get("requestId"));
            request.setAmount(Integer.parseInt(params.get("amount")));
            request.setOrderInfo(params.get("orderInfo"));
            request.setOrderType(params.get("orderType"));
            request.setTransId(params.get("transId"));
            request.setResultCode(Integer.parseInt(params.get("resultCode")));
            request.setMessage(params.get("message"));
            request.setPayType(params.get("payType"));
            request.setResponseTime(params.get("responseTime"));
            request.setExtraData(params.get("extraData"));
            request.setSignature(params.get("signature"));

            // Xử lý callback
            paymentService.handleMoMoCallback(request);

            // Trả về success cho MoMo
            return ResponseEntity.ok("{\"status\":0,\"message\":\"Success\"}");

        } catch (Exception e) {
            log.error("Error processing MoMo IPN: ", e);
            return ResponseEntity.ok("{\"status\":1,\"message\":\"Error\"}");
        }
    }
}