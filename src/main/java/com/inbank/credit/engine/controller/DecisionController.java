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

    @PostMapping("/make/decision")
    public DecisionResponse makeDecision(@Valid DecisionRequest request) {
        var decisionValue = decisionService.findMaximumPossibleLoan(
                request.personalCode(),
                request.loanAmount(),
                request.months()
        );

        if (decisionValue < 0) {
            throw new DeclineException("Negative decision");
        }

        return new DecisionResponse(decisionValue);
    }

    @ExceptionHandler(DeclineException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDeclineException(DeclineException declineException) {
        return new ErrorResponse(declineException.getMessage());
    }
}
