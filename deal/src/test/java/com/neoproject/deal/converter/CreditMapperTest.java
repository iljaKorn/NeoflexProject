package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.CreditDto;
import com.neoproject.deal.model.entity.Credit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты маппера для сущности Кредит")
@ExtendWith(MockitoExtension.class)
class CreditMapperTest {

    private final CreditMapper creditMapper = new CreditMapperImpl();

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