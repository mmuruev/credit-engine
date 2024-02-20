package com.inbank.credit.engine.service;

import com.inbank.credit.engine.domain.LoanRepository;
import com.inbank.credit.engine.domain.document.LoanDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DecisionService {

    final private LoanRepository loanRepository;

    final private static int IN_DEBT_VALUE = -1;
    final private static int MIN_LOAN_AMOUNT = 2000;
    final private static int MAX_LOAN_AMOUNT = 10000;

    final private static int MIN_LOAN_PERIOD = 12;
    final private static int MAX_LOAN_PERIOD = 60;

    public int findMaximumPossibleLoan(String personCode, int loanAmount, int loanPeriod) {
        LoanDocument loanDocument = loanRepository.findByPersonalCode(personCode);

        if (loanDocument.getModifier() == IN_DEBT_VALUE) {
            return IN_DEBT_VALUE;
        }

        int creditModifier = loanDocument.getModifier();

        var creditAmount = findMaximumApprovedAmount(creditModifier, loanAmount, loanPeriod);

        if (creditAmount == IN_DEBT_VALUE) {
            return IN_DEBT_VALUE;
        }

        return findNewPeriodAmount(creditModifier, loanPeriod);
    }

    private int findNewPeriodAmount(int creditModifier, int loanPeriod) {
        for (int period = loanPeriod + 1; period <= MAX_LOAN_PERIOD; period++) {
            for (int amount = MAX_LOAN_AMOUNT; amount >= MIN_LOAN_AMOUNT; amount--) {
                double creditScore = calculateCreditScore(creditModifier, amount, period);

                if (creditScore >= 1) {
                    return amount;
                }
            }
        }

        return IN_DEBT_VALUE;
    }

    private int findMaximumApprovedAmount(int creditModifier, int loanAmount, int loanPeriod) {
        for (int amount = loanAmount; amount >= MIN_LOAN_AMOUNT; amount--) {
            double creditScore = calculateCreditScore(creditModifier, amount, loanPeriod);
            if (creditScore >= 1) {
                return amount;
            }
        }

        return IN_DEBT_VALUE;
    }

    private int calculateCreditScore(int creditModifier, int loanAmount, int loanPeriod) {

        if (creditModifier == IN_DEBT_VALUE) {
            return IN_DEBT_VALUE;
        } else {
            float creditScore = ((float) creditModifier / loanAmount) * loanPeriod;
            return creditScore >= 1 ? Math.round(creditScore) : IN_DEBT_VALUE;
        }
    }

}
