package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.LoanStatementRequestDto;
import com.neoproject.deal.model.entity.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClientMapperTest {

    @Autowired
    private ClientMapper clientMapper;

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