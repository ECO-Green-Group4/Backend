package com.evmarket.trade.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class ContractAddOnsRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotEmpty(message = "At least one service ID is required")
    private List<Long> serviceIds;

    @Pattern(regexp = "^(BUYER|SELLER)$", message = "chargedTo must be BUYER or SELLER")
    private String chargedTo; // applies to all serviceIds in this batch
}


