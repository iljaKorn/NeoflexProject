package com.neoproject.deal.service;

import com.neoproject.deal.converter.ClientMapper;
import com.neoproject.deal.converter.CreditMapper;
import com.neoproject.deal.converter.ScoringDataMapper;
import com.neoproject.deal.converter.StatementMapper;
import com.neoproject.deal.exception.DealDatabaseNotFoundException;
import com.neoproject.deal.exception.InvalidSesCodeException;
import com.neoproject.deal.model.dto.*;
import com.neoproject.deal.model.entity.Client;
import com.neoproject.deal.model.entity.Credit;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.*;
import com.neoproject.deal.producer.EmailProducer;
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
    private StatementMapper statementMapper;

    @Mock
    private CalculatorClientService calculatorClientService;

    @Mock
    private EmailProducer emailProducer;

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
        when(calculatorClientService.getOffers(any(LoanStatementRequestDto.class))).thenReturn(expectedOffers);

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
    void shouldUpdateStatusAndSaveOfferCorrectly() {
        // Подготовка
        Statement statementFromDB = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statementFromDB.setClient(client);

        Statement expectedStatement = new Statement();
        expectedStatement.setStatus(ApplicationStatus.APPROVED);
        List<StatementStatusHistoryDto> statusHistory = new ArrayList<>();
        StatementStatusHistoryDto newStatus = new StatementStatusHistoryDto();
        newStatus.setStatus(String.valueOf(ApplicationStatus.APPROVED));
        newStatus.setTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        newStatus.setChangeType(ChangeType.AUTOMATIC);
        statusHistory.add(newStatus);
        expectedStatement.setStatusHistory(statusHistory);

        when(statementRepository.findByIdWithLock(any()))
                .thenReturn(Optional.of(statementFromDB));
        when(statementRepository.save(any(Statement.class)))
                .thenReturn(expectedStatement);
        doNothing().when(emailProducer).produceMessageForFinishRegistration(any(), any());

        // Действие
        dealService.selectOffer(validRequestForSelectOneOffer);

        // Проверка
        assertThat(statementFromDB.getStatus()).isEqualTo(expectedStatement.getStatus());
        assertThat(statementFromDB.getAppliedOffer()).isNotNull();
        assertThat(statementFromDB.getStatusHistory()).isNotNull();
        assertThat(statementFromDB.getStatusHistory()).isNotEmpty();
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForCalculateCredit() {
        // Подготовка
        when(statementRepository.findByIdWithLock(any()))
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
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statement.setClient(client);

        ScoringDataDto scoringDataDto = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();
        Credit credit = new Credit();

        when(statementRepository.findById(any()))
                .thenReturn(Optional.of(statement));
        doNothing().when(statementMapper).updateStatementFromDto(any(), any(FinishRegistrationRequestDto.class));
        when(scoringDataMapper.toDto(any(Statement.class))).thenReturn(scoringDataDto);
        when(creditMapper.toEntity(any(CreditDto.class))).thenReturn(credit);
        when(calculatorClientService.calculateCredit(any(ScoringDataDto.class))).thenReturn(creditDto);

        when(creditRepository.save(any(Credit.class))).thenReturn(credit);
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);

        doNothing().when(emailProducer).produceMessageForCreateDocument(any(), any());

        // Действие
        dealService.finishRegistration(UUID.randomUUID().toString(), validRequestForFinishRegistration);

        // Проверка
        assertThat(statement.getCredit().getCreditStatus()).isEqualTo(CreditStatus.CALCULATED);
        assertThat(statement.getStatus()).isEqualTo(ApplicationStatus.CC_APPROVED);
        assertThat(statement.getStatusHistory()).hasSize(1);
        assertThat(statement.getStatusHistory().getFirst().getStatus()).isEqualTo(String.valueOf(ApplicationStatus.CC_APPROVED));
        assertThat(statement.getStatusHistory().getFirst().getTime()).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertThat(statement.getStatusHistory().getFirst().getChangeType()).isEqualTo(ChangeType.AUTOMATIC);
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForFinishRegistration(){
        // Подготовка
        when(statementRepository.findById(any()))
                .thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.finishRegistration(UUID.randomUUID().toString(), validRequestForFinishRegistration))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }

    @Test
    void shouldSendDocumentsCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statement.setClient(client);
        statement.setStatementId(UUID.randomUUID());

        when(statementRepository.findById(any())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        doNothing().when(emailProducer).produceMessageForSendDocuments(any(), any());

        // Действие
        dealService.sendDocuments(UUID.randomUUID().toString());

        // Проверка
        assertThat(statement.getStatusHistory()).hasSize(2);
        assertThat(statement.getStatusHistory().getFirst().getStatus()).isEqualTo(String.valueOf(ApplicationStatus.PREPARE_DOCUMENTS));
        assertThat(statement.getStatusHistory().get(1).getStatus()).isEqualTo(String.valueOf(ApplicationStatus.DOCUMENT_CREATED));
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForSendDocument() {
        // Подготовка
        when(statementRepository.findById(any())).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.sendDocuments(UUID.randomUUID().toString()))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }

    @Test
    void shouldRequestForSignDocumentsCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statement.setClient(client);
        statement.setStatementId(UUID.randomUUID());

        when(statementRepository.findById(any())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        doNothing().when(emailProducer).produceMessageForRequestToSign(any(), any(), any());

        // Действие
        dealService.requestForSignDocuments(UUID.randomUUID().toString());

        // Проверка
        assertThat(statement.getSesCode().length()).isEqualTo(6);
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForRequestToSignDocument() {
        // Подготовка
        when(statementRepository.findById(any())).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.requestForSignDocuments(UUID.randomUUID().toString()))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }

    @Test
    void shouldSignDocumentsCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statement.setClient(client);

        Credit credit = new Credit();
        statement.setCredit(credit);
        statement.setSesCode("123456");
        statement.setStatementId(UUID.randomUUID());

        when(statementRepository.findById(any())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        doNothing().when(emailProducer).produceMessageForSignDocuments(any(), any());

        // Действие
        dealService.signDocuments(UUID.randomUUID().toString(), "123456");

        // Проверка
        assertThat(statement.getStatusHistory()).hasSize(2);
        assertThat(statement.getStatusHistory().getFirst().getStatus()).isEqualTo(ApplicationStatus.DOCUMENT_SIGNED.name());
        assertThat(statement.getStatusHistory().get(1).getStatus()).isEqualTo(ApplicationStatus.CREDIT_ISSUED.name());
        assertThat(statement.getSignDate()).isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        assertThat(statement.getCredit().getCreditStatus()).isEqualTo(CreditStatus.ISSUED);
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForSignDocument() {
        // Подготовка
        when(statementRepository.findById(any())).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.signDocuments(UUID.randomUUID().toString(), any()))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }

    @Test
    void shouldThrowInvalidSesCodeExceptionForSignDocument() {
        // Подготовка
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());
        statement.setSesCode("123456");
        when(statementRepository.findById(any())).thenReturn(Optional.of(statement));

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.signDocuments(UUID.randomUUID().toString(), "123455"))
                .isInstanceOf(InvalidSesCodeException.class)
                .hasMessageContaining("Не совпадает ses код");
    }

    @Test
    void shouldRejectStatementCorrectly() {
        // Подготовка
        Statement statement = new Statement();
        Client client = new Client();
        client.setFirstName("ivan");
        client.setLastName("petrov");
        client.setEmail("ivan@mail.ru");
        statement.setClient(client);

        when(statementRepository.findById(any())).thenReturn(Optional.of(statement));
        when(statementRepository.save(any(Statement.class))).thenReturn(statement);
        doNothing().when(emailProducer).produceMessageForRejectStatement(any(), any());

        // Действие
        dealService.rejectStatement(UUID.randomUUID().toString());

        // Проверка
        assertThat(statement.getStatusHistory()).hasSize(1);
        assertThat(statement.getStatusHistory().getFirst().getStatus()).isEqualTo(ApplicationStatus.CLIENT_DENIED.name());
    }

    @Test
    void shouldThrowDealDatabaseNotFoundExceptionForRejectStatement() {
        // Подготовка
        when(statementRepository.findById(any())).thenReturn(Optional.empty());

        // Действие и Проверка
        assertThatThrownBy(() -> dealService.rejectStatement(UUID.randomUUID().toString()))
                .isInstanceOf(DealDatabaseNotFoundException.class)
                .hasMessageContaining("Заявка не найдена");
    }
}