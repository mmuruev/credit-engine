package com.inbank.credit.engine.service;

import com.inbank.credit.engine.domain.document.LoanDocument;
import com.inbank.credit.engine.domain.repository.LoanRepository;
import com.inbank.credit.engine.error.DeclineException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionService {

    private final LoanRepository loanRepository;

    private final static int REJECT_DEBT_VALUE = -1;
    private final static int MIN_LOAN_AMOUNT = 2000;
    private final static int MAX_LOAN_AMOUNT = 10000;

    private final static int MIN_LOAN_PERIOD = 12;
    private final static int MAX_LOAN_PERIOD = 60;

    public Decision findMaximumPossibleLoan(String personCode, int loanAmount, int loanPeriod) {
        LoanDocument loanDocument = loanRepository.findByPersonalCode(personCode)
                .orElseThrow(() -> new DeclineException("No score for client")); // I suppose if person not found, no credit!!

        var allowedLoanAmount = inRangeValue(loanAmount, MIN_LOAN_AMOUNT, MAX_LOAN_AMOUNT);
        var allowedLoanPeriod = inRangeValue(loanPeriod, MIN_LOAN_PERIOD, MAX_LOAN_PERIOD);

        if (loanDocument.getModifier() == REJECT_DEBT_VALUE) {
            log.warn("User already in debt {}", personCode);
            return Decision.ofRejected();
        }

        int creditModifier = loanDocument.getModifier();

        return findNewPeriodAmount(creditModifier, allowedLoanAmount, allowedLoanPeriod);
    }

    private int inRangeValue(int actual, int min, int max) {
        return Math.max(Math.min(actual, max), min);
    }

    private Decision findNewPeriodAmount(int creditModifier, int loanAmount, int loanPeriod) {
        // Start from request period and try to extend it for find success!
        for (int period = loanPeriod; period <= MAX_LOAN_PERIOD; period++) {
            var approvedAmount = findMaximumApprovedAmount(creditModifier, loanAmount, period);

            if (approvedAmount != REJECT_DEBT_VALUE) {
                return new Decision(approvedAmount, period);
            }
        }

        return Decision.ofRejected();
    }


    private int findMaximumApprovedAmount(int creditModifier, int loanAmount, int loanPeriod) {
        // Start from max request loan amount and take score, if not decrease and take the first one witch pass
        for (int amount = loanAmount; amount >= MIN_LOAN_AMOUNT; amount--) {
            var creditScore = calculateCreditScore(creditModifier, amount, loanPeriod);

            if (creditScore > 1.0) {
                return findBiggestAmount(creditModifier, loanAmount, loanPeriod);
            } else if (creditScore == 1.0) {
                return amount;
            }
        }

        return REJECT_DEBT_VALUE;
    }

    private int findBiggestAmount(int creditModifier, int loanAmount, int loanPeriod) {
        for (int amount = loanAmount; amount <= MAX_LOAN_AMOUNT; amount++) {
            var creditScore = calculateCreditScore(creditModifier, amount, loanPeriod);

            if (creditScore < 1.0) {
                return amount - 1;
            }
        }

        return MAX_LOAN_AMOUNT;
    }

    private float calculateCreditScore(int creditModifier, int loanAmount, int loanPeriod) {

        if (creditModifier == REJECT_DEBT_VALUE) {
            return REJECT_DEBT_VALUE;
        } else {
            return ((float) creditModifier / loanAmount) * loanPeriod;

        }
    }


    public record Decision(int loanAmount, int loanPeriod) {
        static Decision ofRejected() {
            return new Decision(REJECT_DEBT_VALUE, 0);
        }
    }

}
