package com.neoproject.deal.service;

import com.neoproject.deal.converter.ClientMapper;
import com.neoproject.deal.converter.CreditMapper;
import com.neoproject.deal.converter.ScoringDataMapper;
import com.neoproject.deal.exception.DealDatabaseNotFoundException;
import com.neoproject.deal.model.dto.*;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Credit;
import com.neoproject.deal.model.entity.Passport;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.*;
import com.neoproject.deal.repository.ClientRepository;
import com.neoproject.deal.repository.CreditRepository;
import com.neoproject.deal.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для сервиса сделок")
@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @InjectMocks
    private DealService dealService;

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private StatementRepository statementRepository;
    @Mock
    private CreditRepository creditRepository;

    @Mock
    private ScoringDataMapper scoringDataMapper;
    @Mock
    private CreditMapper creditMapper;
    @Mock
    private ClientMapper clientMapper;

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private LoanStatementRequestDto validRequestForGetOffers;
    private LoanOfferDto validRequestForSelectOneOffer;
    private FinishRegistrationRequestDto validRequestForFinishRegistration;

    @BeforeEach
    void setUp() {
        validRequestForGetOffers = new LoanStatementRequestDto();
        validRequestForGetOffers.setAmount(new BigDecimal("300000"));
        validRequestForGetOffers.setTerm(12);
        validRequestForGetOffers.setFirstName("Ivan");
        validRequestForGetOffers.setLastName("Petrov");
        validRequestForGetOffers.setMiddleName("Sergeevich");
        validRequestForGetOffers.setEmail("ivan@example.com");
        validRequestForGetOffers.setBirthdate(LocalDate.of(1990, 1, 1));
        validRequestForGetOffers.setPassportSeries("1234");
        validRequestForGetOffers.setPassportNumber("567890");

        validRequestForSelectOneOffer = new LoanOfferDto();
        validRequestForSelectOneOffer.setRequestAmount(new BigDecimal("300000"));
        validRequestForSelectOneOffer.setTotalAmount(new BigDecimal("324929.88"));
        validRequestForSelectOneOffer.setTerm(12);
        validRequestForSelectOneOffer.setMonthlyPayment(new BigDecimal("27077.49"));
        validRequestForSelectOneOffer.setRate(new BigDecimal("15"));
        validRequestForSelectOneOffer.setIsInsuranceEnabled(false);
        validRequestForSelectOneOffer.setIsSalaryClient(false);

        validRequestForFinishRegistration = new FinishRegistrationRequestDto();
        validRequestForFinishRegistration.setGender(Gender.MALE);
        validRequestForFinishRegistration.setMaritalStatus(MaritalStatus.MARRIED);
        validRequestForFinishRegistration.setDependentAmount(2);
        validRequestForFinishRegistration.setPassportIssueDate(LocalDate.of(2010, 6, 20));
        validRequestForFinishRegistration.setPassportIssueBranch("УФМС России по г. Москва");
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        validRequestForFinishRegistration.setEmployment(employment);
        validRequestForFinishRegistration.setAccountNumber("40817810001234567890");
    }

    @Test
    void shouldSaveClientAndStatementCorrectly() {
        // Подготовка
        Client client = new Client();

        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(client);
        statement.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        List<LoanOfferDto> expectedOffers = new ArrayList<>();
        expectedOffers.add(new LoanOfferDto());
        expectedOffers.add(new LoanOfferDto());
        expectedOffers.add(new LoanOfferDto());
        expectedOffers.add(new LoanOfferDto());

        when(clientMapper.toEntity(validRequestForGetOffers)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedOffers);

        // Действие
        List<LoanOfferDto> result = dealService.getOffers(validRequestForGetOffers);

        // Проверка
        assertThat(result.get(0).getStatementId()).isEqualTo(statement.getStatementId());
        assertThat(result.get(1).getStatementId()).isEqualTo(statement.getStatementId());
        assertThat(result.get(2).getStatementId()).isEqualTo(statement.getStatementId());
        assertThat(result.get(3).getStatementId()).isEqualTo(statement.getStatementId());

        verify(statementRepository, times(1)).save(any(Statement.class));
        verify(clientRepository, times(1)).save(any(Client.class));
        verify(clientMapper, times(1)).toEntity(any(LoanStatementRequestDto.class));
    }

    @Test
    void shouldThrowDealServiceExceptionForEternalService() {
        // Подготовка
        Client client = new Client();

        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(client);
        statement.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        when(clientMapper.toEntity(validRequestForGetOffers)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.getOffers(validRequestForGetOffers))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Ошибка при запросе в другой сервис");
    }

    @Test
    void shouldThrowDealServiceExceptionForResponseWithNull() {
        // Подготовка
        Client client = new Client();

        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setClient(client);
        statement.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        when(clientMapper.toEntity(validRequestForGetOffers)).thenReturn(client);
        when(clientRepository.save(client)).thenReturn(client);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/offers")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(validRequestForGetOffers)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(null);

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.getOffers(validRequestForGetOffers))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Полученные данные равны null");
    }

    @Test
    void shouldUpdateStatusAndSaveOfferCorrectly() {
        // Подготовка
        Statement statementFromDB = new Statement();

        Statement expectedStatement = new Statement();
        expectedStatement.setStatus(ApplicationStatus.PREAPPROVAL);
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        StatementStatusHistoryDto newStatus = new StatementStatusHistoryDto();
        newStatus.setStatus(String.valueOf(ApplicationStatus.PREAPPROVAL));
        newStatus.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        newStatus.setChangeType(ChangeType.AUTOMATIC);
        statusHistory.add(newStatus);
        expectedStatement.setStatusHistory(statusHistory);

        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statementFromDB));
        when(statementRepository.save(any(Statement.class)))
                .thenReturn(expectedStatement);

        // Действие
        dealService.selectOffer(validRequestForSelectOneOffer);

        // Проверка
        assertThat(statementFromDB.getStatus()).isEqualTo(expectedStatement.getStatus());
        assertThat(statementFromDB.getAppliedOffer()).isNotNull();
        assertThat(statementFromDB.getStatusHistory()).isNotNull();
        assertThat(statementFromDB.getStatusHistory()).isNotEmpty();
    }

    @Test
    void shouldThrowDealServiceExceptionForNotFoundInDB() {
        // Подготовка
        when(statementRepository.findById(any()))
                .thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.selectOffer(validRequestForSelectOneOffer))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }

    @Test
    void shouldFinishRegistrationCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        Passport passport = new Passport();
        client.setPassport(passport);
        statement.setClient(client);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();

        Credit expectedCredit = new Credit();
        expectedCredit.setCreditStatus(CreditStatus.CALCULATED);
        Statement expectedStatement = new Statement();
        Client expectedClient = new Client();
        expectedClient.setGender(Gender.MALE);
        expectedClient.setMaritalStatus(MaritalStatus.MARRIED);
        expectedClient.setDependentAmount(2);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        expectedClient.setEmployment(employment);
        expectedClient.setAccountNumber("40817810001234567890");
        expectedStatement.setClient(expectedClient);
        expectedStatement.setCredit(expectedCredit);

        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        when(scoringDataMapper.toDto(any(Statement.class))).thenReturn(scoringDataDto);
        when(creditMapper.toEntity(any(CreditDto.class))).thenReturn(new Credit());

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(scoringDataDto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class)).thenReturn(creditDto);

        when(creditRepository.save(any())).thenReturn(expectedCredit);
        when(statementRepository.save(any())).thenReturn(expectedStatement);

        // Действие
        dealService.finishRegistration(UUID.randomUUID().toString(), validRequestForFinishRegistration);

        // Проверка
        assertThat(statement.getClient().getGender()).isEqualTo(expectedStatement.getClient().getGender());
        assertThat(statement.getClient().getEmployment()).isEqualTo(expectedStatement.getClient().getEmployment());
        assertThat(statement.getClient().getDependentAmount()).isEqualTo(expectedStatement.getClient().getDependentAmount());
        assertThat(statement.getClient().getMaritalStatus()).isEqualTo(expectedStatement.getClient().getMaritalStatus());
        assertThat(statement.getClient().getAccountNumber()).isEqualTo(expectedStatement.getClient().getAccountNumber());
    }

    @Test
    void shouldThrowDealServiceExceptionForEternalServiceCalc() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        Passport passport = new Passport();
        client.setPassport(passport);
        statement.setClient(client);

        ScoringDataDto scoringDataDto = new ScoringDataDto();

        Credit expectedCredit = new Credit();
        expectedCredit.setCreditStatus(CreditStatus.CALCULATED);
        Statement expectedStatement = new Statement();
        Client expectedClient = new Client();
        expectedClient.setGender(Gender.MALE);
        expectedClient.setMaritalStatus(MaritalStatus.MARRIED);
        expectedClient.setDependentAmount(2);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        expectedClient.setEmployment(employment);
        expectedClient.setAccountNumber("40817810001234567890");
        expectedStatement.setClient(expectedClient);
        expectedStatement.setCredit(expectedCredit);

        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        when(scoringDataMapper.toDto(any(Statement.class))).thenReturn(scoringDataDto);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(scoringDataDto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class))
                .thenThrow(new RuntimeException("Connection refused"));

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.finishRegistration(UUID.randomUUID().toString(), validRequestForFinishRegistration))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Ошибка при запросе в другой сервис");
    }

    @Test
    void shouldThrowDealServiceExceptionForNullResponse() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        Passport passport = new Passport();
        client.setPassport(passport);
        statement.setClient(client);

        ScoringDataDto scoringDataDto = new ScoringDataDto();

        Credit expectedCredit = new Credit();
        expectedCredit.setCreditStatus(CreditStatus.CALCULATED);
        Statement expectedStatement = new Statement();
        Client expectedClient = new Client();
        expectedClient.setGender(Gender.MALE);
        expectedClient.setMaritalStatus(MaritalStatus.MARRIED);
        expectedClient.setDependentAmount(2);
        EmploymentDto employment = new EmploymentDto();
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);
        employment.setEmployerINN("123456789012");
        employment.setSalary(BigDecimal.valueOf(85000));
        employment.setPosition(Position.MID_MANAGER);
        employment.setWorkExperienceTotal(60);
        employment.setWorkExperienceCurrent(36);
        expectedClient.setEmployment(employment);
        expectedClient.setAccountNumber("40817810001234567890");
        expectedStatement.setClient(expectedClient);
        expectedStatement.setCredit(expectedCredit);

        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        when(scoringDataMapper.toDto(any(Statement.class))).thenReturn(scoringDataDto);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/calc")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(scoringDataDto)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(CreditDto.class))
                .thenReturn(null);

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.finishRegistration(UUID.randomUUID().toString(), validRequestForFinishRegistration))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Полученные данные равны null");
    }
}