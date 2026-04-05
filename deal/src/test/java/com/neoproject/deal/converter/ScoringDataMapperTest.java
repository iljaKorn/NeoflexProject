package com.neoproject.deal.converter;

import com.neoproject.deal.model.dto.EmploymentDto;
import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.dto.ScoringDataDto;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Passport;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ScoringDataMapperTest {

    private final ScoringDataMapper scoringDataMapper = new ScoringDataMapperImpl();

    @Test
    void shouldConvertStatementIntoScoringDataDto() {
        // Подготовка
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        Client client = new Client();
        client.setGender(Gender.MALE);
        client.setFirstName("Ivan");
        client.setLastName("Petrov");
        client.setMiddleName("Alekseevich");
        client.setBirthdate(LocalDate.of(1990, 1, 1));
        Passport passport = new Passport();
        passport.setSeries("1234");
        passport.setNumber("123456");
        passport.setIssueDate(LocalDate.of(2010, 6, 20));
        passport.setIssueBranch("УФМС России по г. Москва");
        client.setPassport(passport);
        client.setMaritalStatus(MaritalStatus.DIVORCED);
        client.setDependentAmount(2);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        client.setEmployment(employment);
        client.setAccountNumber("1234345565756767");
        LoanOfferDto offer = new LoanOfferDto();
        offer.setRequestAmount(new BigDecimal("300000"));
        offer.setTerm(12);
        offer.setIsInsuranceEnabled(false);
        offer.setIsSalaryClient(false);
        statement.setAppliedOffer(offer);
        statement.setClient(client);

        // Действие
        ScoringDataDto scoringDataDto = scoringDataMapper.toDto(statement);

        // Проверка
        assertThat(scoringDataDto.getGender()).isEqualTo(statement.getClient().getGender());
        assertThat(scoringDataDto.getFirstName()).isEqualTo(statement.getClient().getFirstName());
        assertThat(scoringDataDto.getLastName()).isEqualTo(statement.getClient().getLastName());
        assertThat(scoringDataDto.getMiddleName()).isEqualTo(statement.getClient().getMiddleName());
        assertThat(scoringDataDto.getBirthdate()).isEqualTo(statement.getClient().getBirthdate());
        assertThat(scoringDataDto.getMaritalStatus()).isEqualTo(statement.getClient().getMaritalStatus());
        assertThat(scoringDataDto.getDependentAmount()).isEqualTo(statement.getClient().getDependentAmount());
        assertThat(scoringDataDto.getAccountNumber()).isEqualTo(statement.getClient().getAccountNumber());
        assertThat(scoringDataDto.getEmployment()).isEqualTo(statement.getClient().getEmployment());

        assertThat(scoringDataDto.getPassportNumber()).isEqualTo(statement.getClient().getPassport().getNumber());
        assertThat(scoringDataDto.getPassportSeries()).isEqualTo(statement.getClient().getPassport().getSeries());
        assertThat(scoringDataDto.getPassportIssueBranch()).isEqualTo(statement.getClient().getPassport().getIssueBranch());
        assertThat(scoringDataDto.getPassportIssueDate()).isEqualTo(statement.getClient().getPassport().getIssueDate());

        assertThat(scoringDataDto.getAmount()).isEqualTo(statement.getAppliedOffer().getRequestAmount());
        assertThat(scoringDataDto.getTerm()).isEqualTo(statement.getAppliedOffer().getTerm());
        assertThat(scoringDataDto.getIsInsuranceEnabled()).isEqualTo(statement.getAppliedOffer().getIsInsuranceEnabled());
        assertThat(scoringDataDto.getIsSalaryClient()).isEqualTo(statement.getAppliedOffer().getIsSalaryClient());
    }
}