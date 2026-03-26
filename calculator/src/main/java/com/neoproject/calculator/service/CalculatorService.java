package com.neoproject.calculator.service;

import com.neoproject.calculator.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Сервис калькулятора
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculatorService {

    @Value("${bank.rate}")
    private BigDecimal startRate;
    @Value("${bank.insurance.price}")
    private BigDecimal insurancePrice;
    @Value("${bank.insurance.discount}")
    private BigDecimal insuranceDiscount;
    @Value("${bank.salary.discount}")
    private BigDecimal salaryDiscount;

    private final ScoringService scoringService;

    /**
     * Метод для получения всех возможных условий кредита
     * @param dto специальный объект со всеми входными данными для составления различных условий кредита
     */
    public List<LoanOfferDto> getOffers(LoanStatementRequestDto dto) {
        List<LoanOfferDto> offers = new ArrayList<>();
        boolean[] options = {true, false};

        BigDecimal clientRate = startRate;
        BigDecimal totalAmount = dto.getAmount();
        for (boolean isInsuranceEnabled : options) {
            for (boolean isSalaryClient : options) {
                if (isInsuranceEnabled) {
                    totalAmount = totalAmount.add(insurancePrice);
                    clientRate = clientRate.subtract(insuranceDiscount);
                }
                if (isSalaryClient) {
                    clientRate = clientRate.subtract(salaryDiscount);
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

                clientRate = startRate;
                totalAmount = dto.getAmount();
            }
        }

        offers.sort(Comparator.comparing(LoanOfferDto::getRate).reversed());

        return offers;
    }

    /**
     * Метод для полного расчета всех параметров кредита
     * @param dto специальный объект с всеми входными данными для расчёта параметров кредита
     */
    public CreditDto calculateCredit(ScoringDataDto dto) {
        int scoreRate = scoringService.scoring(dto);
        log.debug("Скоринг пройден для клиента {} {}", dto.getFirstName(), dto.getLastName());

        BigDecimal clientRate = startRate.add(new BigDecimal(scoreRate));
        BigDecimal amount = dto.getAmount();

        if (dto.getIsInsuranceEnabled()) {
            amount = amount.add(insurancePrice);
            clientRate = clientRate.subtract(insuranceDiscount);
        }
        if (dto.getIsSalaryClient()) {
            clientRate = clientRate.subtract(salaryDiscount);
        }

        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, clientRate, dto.getTerm());

        CreditDto creditDto = new CreditDto();
        creditDto.setAmount(dto.getAmount());
        creditDto.setTerm(dto.getTerm());
        creditDto.setMonthlyPayment(monthlyPayment);
        creditDto.setRate(clientRate);
        creditDto.setPsk(monthlyPayment.multiply(BigDecimal.valueOf(dto.getTerm())));
        creditDto.setIsInsuranceEnabled(dto.getIsInsuranceEnabled());
        creditDto.setIsSalaryClient(dto.getIsSalaryClient());
        creditDto.setPaymentSchedule(createPaymentSchedule(dto.getAmount(), clientRate, dto.getTerm()));

        return creditDto;
    }

    /**
     * Вспомогательный метод для расчёта ежемесячного платежа по аннуитетной схеме (равными платежами)
     * @param totalAmount итоговая сумма долга заемщика перед банком
     * @param rate итоговая ставка по кредиту
     * @param term срок по выплате кредита
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal totalAmount, BigDecimal rate, Integer term) {
        // Формула расчёта аннуитетного платежа
        // Платеж = Сумма кредита × (мес.ставка × (1 + мес. Ставка)^Срок) / ((1 + мес. Ставка)^Срок - 1)
        BigDecimal monthlyRate = rate.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal pow = onePlusRate.pow(term);

        BigDecimal numerator = monthlyRate.multiply(pow);
        BigDecimal denominator = pow.subtract(BigDecimal.ONE);
        BigDecimal annuityCoefficient = numerator.divide(denominator, 10, RoundingMode.HALF_UP);

        return totalAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Вспомогательный метод для расчёта графика платежей
     * @param amount итоговая сумма долга заемщика перед банком
     * @param rate итоговая ставка по кредиту
     * @param term срок по выплате кредита
     */
    private List<PaymentScheduleElementDto> createPaymentSchedule(BigDecimal amount, BigDecimal rate, Integer term) {
        List<PaymentScheduleElementDto> list = new ArrayList<>();
        LocalDate date = LocalDate.now();
        BigDecimal monthlyPayment = calculateMonthlyPayment(amount, rate, term);
        BigDecimal remainingDebt = amount;

        for (int i = 1; i <= term; i++) {
            PaymentScheduleElementDto dto = new PaymentScheduleElementDto();
            dto.setNumber(i);

            dto.setDate(date);
            date = date.plusMonths(1);

            BigDecimal interestPayment = remainingDebt.multiply(rate.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setInterestPayment(interestPayment);

            BigDecimal debtPayment;
            // из-за погрешности вычислений в последнем платеже мог возникать отрицательный остаток по долгу,
            // поэтому в последнем платеже списываем весь долг
            if (i == term) {
                debtPayment = remainingDebt;
                dto.setTotalPayment(debtPayment.add(interestPayment));
            } else {
                debtPayment = monthlyPayment.subtract(interestPayment)
                        .setScale(2, RoundingMode.HALF_UP);
                dto.setTotalPayment(monthlyPayment);
            }
            dto.setDebtPayment(debtPayment);

            remainingDebt = remainingDebt.subtract(debtPayment).setScale(2, RoundingMode.HALF_UP);
            dto.setRemainingDebt(remainingDebt);

            list.add(dto);
        }

        return list;
    }
}
