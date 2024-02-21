package com.inbank.credit.engine.controller;

import com.inbank.credit.engine.dto.DecisionRequest;
import com.inbank.credit.engine.dto.DecisionResponse;
import com.inbank.credit.engine.error.DeclineException;
import com.inbank.credit.engine.error.ErrorResponse;
import com.inbank.credit.engine.service.DecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api")
public class DecisionController {

    final private DecisionService decisionService;

    @PostMapping("/decision/make")
    public DecisionResponse makeDecision(@RequestBody @Valid DecisionRequest request) {
        var decision = decisionService.findMaximumPossibleLoan(
                request.personalCode(),
                request.loanAmount(),
                request.months()
        );

        if (decision.loanAmount() < 0) {
            throw new DeclineException("Negative decision");
        }

        log.info("Approved load for user {}, Loan: {}", request.personalCode(), decision);

        return new DecisionResponse(decision.loanAmount(), decision.loanPeriod());
    }

    @ExceptionHandler(DeclineException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDeclineException(DeclineException declineException) {
        return new ErrorResponse(declineException.getMessage());
    }
}
