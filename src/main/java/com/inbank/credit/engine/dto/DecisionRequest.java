package com.inbank.credit.engine.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DecisionRequest(
        @JsonProperty("personalCode")
        String personalCode,
        @JsonProperty("loanAmount")
        Integer loanAmount,
        @JsonProperty("months")
        Integer months
) {
}
