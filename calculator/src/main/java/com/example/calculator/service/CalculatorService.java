package com.example.calculator.service;

import com.example.calculator.exception.PreScoringException;
import com.example.calculator.exception.ScoringException;
import com.example.calculator.model.DTO.CreditDto;
import com.example.calculator.model.DTO.LoanOfferDto;
import com.example.calculator.model.DTO.LoanStatementRequestDto;
import com.example.calculator.model.DTO.ScoringDataDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final ScoringService scoringService;

    @Value("${bank.rate}")
    private BigDecimal START_RATE;
    private static final BigDecimal INSURANCE_PRICE = new BigDecimal("100000");
    private static final BigDecimal INSURANCE_DISCOUNT = new BigDecimal("3.0");
    private static final BigDecimal SALARY_DISCOUNT = new BigDecimal("1.0");

    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        if (!scoringService.preScoring(dto)) {
            throw new PreScoringException("Данные не прошли прескоринг");
        }

        List<LoanOfferDto> offers = new ArrayList<>();
        boolean[] options = {true, false};

        BigDecimal clientRate = START_RATE;
        BigDecimal totalAmount = dto.getAmount();
        for (boolean isInsuranceEnabled : options) {
            for (boolean isSalaryClient : options) {
                if (isInsuranceEnabled) {
                    totalAmount = totalAmount.add(INSURANCE_PRICE);
                    clientRate = clientRate.subtract(INSURANCE_DISCOUNT);
                }
                if (isSalaryClient) {
                    clientRate = clientRate.subtract(SALARY_DISCOUNT);
                }

                BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, clientRate, dto.getTerm());

                LoanOfferDto offer = new LoanOfferDto();
                offer.setStatementId(UUID.randomUUID());
                offer.setRequestAmount(dto.getAmount());
                offer.setTotalAmount(monthlyPayment.multiply(BigDecimal.valueOf(dto.getTerm())));
                offer.setTerm(dto.getTerm());
                offer.setMonthlyPayment(monthlyPayment);
                offer.setRate(clientRate);
                offer.setIsInsuranceEnabled(isInsuranceEnabled);
                offer.setIsSalaryClient(isSalaryClient);

                offers.add(offer);

                clientRate = START_RATE;
                totalAmount = dto.getAmount();
            }
        }

        offers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());

        return offers;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate, Integer term) {
        // формула расчёта аннуиетного платежа
        // Платеж = Сумма кредита × (мес.ставка × (1 + мес.ставка)^Срок) / ((1 + мес.ставка)^Срок - 1)
        BigDecimal monthlyRate = rate.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusRate.pow(term);

        BigDecimal numerator = monthlyRate.multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        BigDecimal annuityCoefficient = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

        return totalAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.HALF_UP);
    }

    public CreditDto calculateCredit(ScoringDataDto dto) {
        int scoreRate = scoringService.scoring(dto);
        if (scoreRate == 0) {
            throw new ScoringException("Данные не прошли скоринг");
        }
        BigDecimal clientRate = START_RATE.add(new BigDecimal(String.valueOf(scoreRate)));

        return null;
    }
}
