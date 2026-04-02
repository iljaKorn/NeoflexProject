package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.CreditDto;
import com.neoproject.deal.model.entity.Credit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreditMapperTest {

    @Autowired
    private CreditMapper creditMapper;

    @Test
    void shouldConvertCreditDtoIntoCredit() {
        // Подготовка
        CreditDto creditDto = new CreditDto();
        creditDto.setIsSalaryClient(false);
        creditDto.setIsInsuranceEnabled(false);

        // Действие
        Credit credit = creditMapper.toEntity(creditDto);

        // Проверка
        assertThat(credit.getSalaryClient()).isEqualTo(creditDto.getIsSalaryClient());
        assertThat(credit.getInsuranceEnabled()).isEqualTo(creditDto.getIsInsuranceEnabled());
    }
}