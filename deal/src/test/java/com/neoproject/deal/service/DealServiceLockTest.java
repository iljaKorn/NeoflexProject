package com.neoproject.deal.service;

import com.neoproject.deal.config.EmailProducerConfig;
import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.ApplicationStatus;
import com.neoproject.deal.producer.EmailProducer;
import com.neoproject.deal.repository.StatementRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@Testcontainers
@SpringBootTest
@Slf4j
public class DealServiceLockTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("mydb")
            .withUsername("myuser")
            .withPassword("mypass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }

    @MockitoSpyBean
    private DealService dealService;

    @Autowired
    private StatementRepository statementRepository;

    @MockitoBean
    private CalculatorClientService calculatorClientService;

    @MockitoBean
    private RestClient restClient;

    @MockitoBean
    private KafkaTemplate kafkaTemplate;

    @MockitoBean
    private EmailProducer emailProducer;

    @MockitoBean
    private EmailProducerConfig emailProducerConfig;

    private UUID statementId1;
    private UUID statementId2;

    @BeforeEach
    void setUp() {
        Statement statement1 = new Statement();
        Statement statement2 = new Statement();

        statementId1 = statementRepository.save(statement1).getStatementId();
        statementId2 = statementRepository.save(statement2).getStatementId();
    }

    @Test
    void shouldBlockSecondThreadWhenFirstHoldsPessimisticLockWithSameStatementId() throws Exception {
        // Подготовка
        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setStatementId(statementId1);

        CountDownLatch lockAcquired = new CountDownLatch(1);
        AtomicLong thread2WaitTime = new AtomicLong(-1);

        doAnswer(invocation -> {
            Object result = invocation.callRealMethod();

            lockAcquired.countDown();

            Thread.sleep(2000);

            return result;
        }).when(dealService).selectOffer(offerDto);

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.submit(() -> {
                try {
                    assertThat(lockAcquired.await(5, TimeUnit.SECONDS)).isTrue();
                    long start = System.currentTimeMillis();
                    dealService.selectOffer(offerDto);
                    thread2WaitTime.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) executor.shutdownNow();

            // Проверка
            assertThat(thread2WaitTime.get()).isGreaterThan(4000L);

            Statement stmt = statementRepository.findById(statementId1).orElseThrow();
            assertThat(stmt.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(stmt.getAppliedOffer()).isNotNull();
        }
    }

    @Test
    void shouldNotBlockSecondThreadWhenFirstHoldsPessimisticLockWithDifferentStatementId() throws Exception {
        // Подготовка
        LoanOfferDto offerDto1 = new LoanOfferDto();
        offerDto1.setStatementId(statementId1);

        LoanOfferDto offerDto2 = new LoanOfferDto();
        offerDto2.setStatementId(statementId2);

        CountDownLatch lockAcquired = new CountDownLatch(1);
        AtomicLong thread2WaitTime = new AtomicLong(-1);

        doAnswer(invocation -> {
            Object result = invocation.callRealMethod();

            lockAcquired.countDown();

            Thread.sleep(2000);

            return result;
        }).when(dealService).selectOffer(Mockito.any());

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto1);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.submit(() -> {
                try {
                    assertThat(lockAcquired.await(5, TimeUnit.SECONDS)).isTrue();
                    long start = System.currentTimeMillis();
                    dealService.selectOffer(offerDto2);
                    thread2WaitTime.set(System.currentTimeMillis() - start);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            });

            executor.shutdown();
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) executor.shutdownNow();

            // Проверка
            assertThat(thread2WaitTime.get()).isGreaterThan(2000L);

            Statement statement1 = statementRepository.findById(statementId1).orElseThrow();
            assertThat(statement1.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(statement1.getAppliedOffer()).isNotNull();
            assertThat(statement1.getStatusHistory()).hasSize(1);

            Statement statement2 = statementRepository.findById(statementId1).orElseThrow();
            assertThat(statement2.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(statement2.getAppliedOffer()).isNotNull();
            assertThat(statement2.getStatusHistory()).hasSize(1);
        }
    }
}