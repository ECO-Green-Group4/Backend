package com.evmarket.trade.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContractAddOnsRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotEmpty(message = "At least one service ID is required")
    private List<Long> serviceIds;
}


