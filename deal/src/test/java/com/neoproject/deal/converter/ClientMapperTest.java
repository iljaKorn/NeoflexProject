package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.entity.Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты маппера для сущности Клиент")
@ExtendWith(MockitoExtension.class)
class ClientMapperTest {

    private final ClientMapper clientMapper = new ClientMapperImpl();

    @Test
    void shouldConvertLoanStatementRequestDtoIntoClient() {
        // Подготовка
        LoanStatementRequestDto dto = new LoanStatementRequestDto();
        dto.setPassportNumber("1234");
        dto.setPassportSeries("123456");

        // Действие
        Client client = clientMapper.toEntity(dto);

        // Проверка
        assertThat(client.getPassport().getNumber()).isEqualTo(dto.getPassportNumber());
        assertThat(client.getPassport().getSeries()).isEqualTo(dto.getPassportSeries());
    }
}