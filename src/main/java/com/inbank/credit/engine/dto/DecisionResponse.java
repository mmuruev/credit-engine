package com.inbank.credit.engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DecisionResponse(
        @JsonProperty("approvedAmount")
        int approvedAmount
) {
}
