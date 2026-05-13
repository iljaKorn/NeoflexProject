package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.DocumentDto;
import com.neoproject.deal.model.dto.EmploymentDto;
import com.neoproject.deal.model.dto.FinishRegistrationRequestDto;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Credit;
import com.neoproject.deal.model.entity.Passport;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.Gender;
import com.neoproject.deal.model.enums.MaritalStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("Тесты маппера для сущности Заявка")
@ExtendWith(MockitoExtension.class)
class StatementMapperTest {

    private final StatementMapper statementMapper = new StatementMapperImpl();

    @Test
    void shouldUpdateStatementCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        statement.setClient(client);

        FinishRegistrationRequestDto dto = new FinishRegistrationRequestDto();
        dto.setGender(Gender.MALE);
        dto.setMaritalStatus(MaritalStatus.DIVORCED);
        dto.setDependentAmount(2);
        dto.setEmployment(new EmploymentDto());
        dto.setAccountNumber("1234");
        dto.setPassportIssueDate(LocalDate.now());
        dto.setPassportIssueBranch("УФМС России по г. Москва");

        // Действие
        statementMapper.updateStatementFromDto(statement, dto);

        // Проверка
        assertThat(statement.getClient().getGender()).isEqualTo(dto.getGender());
        assertThat(statement.getClient().getMaritalStatus()).isEqualTo(dto.getMaritalStatus());
        assertThat(statement.getClient().getDependentAmount()).isEqualTo(dto.getDependentAmount());
        assertThat(statement.getClient().getEmployment()).isEqualTo(dto.getEmployment());
        assertThat(statement.getClient().getAccountNumber()).isEqualTo(dto.getAccountNumber());
        assertThat(statement.getClient().getPassport().getIssueDate()).isEqualTo(dto.getPassportIssueDate());
        assertThat(statement.getClient().getPassport().getIssueBranch()).isEqualTo(dto.getPassportIssueBranch());
    }

    @Test
    void shouldCreateDocumentDtoCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        Passport passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("123456");
        client.setPassport(passport);
        statement.setClient(client);

        Credit credit = new Credit();
        credit.setAmount(new BigDecimal("300000"));
        credit.setTerm(12);
        credit.setMonthlyPayment(new BigDecimal("29000"));
        credit.setRate(new BigDecimal("15"));
        statement.setCredit(credit);

        // Действие
        DocumentDto documentDto = statementMapper.toDocumentDto(statement);

        // Проверка
        assertThat(documentDto.getFirstName()).isEqualTo(statement.getClient().getFirstName());
        assertThat(documentDto.getLastName()).isEqualTo(statement.getClient().getLastName());
        assertThat(documentDto.getPassportNumber()).isEqualTo(statement.getClient().getPassport().getNumber());
        assertThat(documentDto.getPassportSeries()).isEqualTo(statement.getClient().getPassport().getSeries());
        assertThat(documentDto.getAmount()).isEqualTo(statement.getCredit().getAmount());
        assertThat(documentDto.getTerm()).isEqualTo(statement.getCredit().getTerm());
        assertThat(documentDto.getMonthlyPayment()).isEqualTo(statement.getCredit().getMonthlyPayment());
        assertThat(documentDto.getRate()).isEqualTo(statement.getCredit().getRate());
    }
}