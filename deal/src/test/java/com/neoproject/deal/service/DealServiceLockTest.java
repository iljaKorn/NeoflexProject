package com.neoproject.deal.service;

import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.ApplicationStatus;
import com.neoproject.deal.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

@Testcontainers
@SpringBootTest
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
    void shouldLockThreadWithSameStatementId() throws Exception {
        // Подготовка
        LoanOfferDto offerDto = new LoanOfferDto();
        offerDto.setStatementId(statementId1);

        doAnswer(new AnswersWithDelay(2000, invocation -> {
            return invocation.callRealMethod();
        })).when(dealService).selectOffer(Mockito.any(LoanOfferDto.class));

        CompletableFuture<Long> firstThreadTiming = new CompletableFuture<>();
        CompletableFuture<Long> secondThreadTiming = new CompletableFuture<>();

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                dealService.selectOffer(offerDto);
                firstThreadTiming.complete(System.currentTimeMillis() - start);
            });

            executor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                long start = System.currentTimeMillis();
                dealService.selectOffer(offerDto);
                secondThreadTiming.complete(System.currentTimeMillis() - start);
            });
        }

        long firstDuration = firstThreadTiming.get(5, TimeUnit.SECONDS);
        long secondDuration = secondThreadTiming.get(5, TimeUnit.SECONDS);

        // Проверка
        assertThat(firstDuration).isBetween(1900L, 2500L);
        assertThat(secondDuration).isGreaterThan(1500L);

        Statement statement = statementRepository.findById(statementId1).orElseThrow();
        assertThat(statement.getStatus()).isEqualTo(ApplicationStatus.PREAPPROVAL);
        assertThat(statement.getAppliedOffer()).isNotNull();
        assertThat(statement.getStatusHistory()).hasSize(1);
    }

    @Test
    void shouldLockThreadWithDifferentStatementId() throws Exception {
        // Подготовка
        LoanOfferDto offerDto1 = new LoanOfferDto();
        offerDto1.setStatementId(statementId1);

        LoanOfferDto offerDto2 = new LoanOfferDto();
        offerDto2.setStatementId(statementId2);

        doAnswer(new AnswersWithDelay(2000, invocation -> {
            return invocation.callRealMethod();
        })).when(dealService).selectOffer(Mockito.any(LoanOfferDto.class));

        CompletableFuture<Long> firstThreadTiming = new CompletableFuture<>();
        CompletableFuture<Long> secondThreadTiming = new CompletableFuture<>();

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(() -> {
                long start = System.currentTimeMillis();
                dealService.selectOffer(offerDto1);
                firstThreadTiming.complete(System.currentTimeMillis() - start);
            });

            executor.submit(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                long start = System.currentTimeMillis();
                dealService.selectOffer(offerDto2);
                secondThreadTiming.complete(System.currentTimeMillis() - start);
            });
        }

        long firstDuration = firstThreadTiming.get(5, TimeUnit.SECONDS);
        long secondDuration = secondThreadTiming.get(5, TimeUnit.SECONDS);

        // Проверка
        assertThat(firstDuration).isBetween(1900L, 2500L);
        assertThat(secondDuration).isGreaterThan(1500L);

        Statement statement1 = statementRepository.findById(statementId1).orElseThrow();
        assertThat(statement1.getStatus()).isEqualTo(ApplicationStatus.PREAPPROVAL);
        assertThat(statement1.getAppliedOffer()).isNotNull();
        assertThat(statement1.getStatusHistory()).hasSize(1);

        Statement statement2 = statementRepository.findById(statementId1).orElseThrow();
        assertThat(statement2.getStatus()).isEqualTo(ApplicationStatus.PREAPPROVAL);
        assertThat(statement2.getAppliedOffer()).isNotNull();
        assertThat(statement2.getStatusHistory()).hasSize(1);
    }
}
