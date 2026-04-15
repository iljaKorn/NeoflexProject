package com.neoproject.deal.service;

import com.neoproject.deal.model.dto.LoanOfferDto;
import com.neoproject.deal.model.entity.Statement;
import com.neoproject.deal.model.enums.ApplicationStatus;
import com.neoproject.deal.repository.StatementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Autowired
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

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch latch = new CountDownLatch(2);

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto);
                } finally {
                    latch.countDown();
                }
            });

            boolean completed = latch.await(10, TimeUnit.SECONDS);

            // Проверка
            assertThat(completed).isTrue();
        }

        // Проверка
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

        // Действие
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            CountDownLatch latch = new CountDownLatch(2);

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto1);
                } finally {
                    latch.countDown();
                }
            });

            executor.submit(() -> {
                try {
                    dealService.selectOffer(offerDto2);
                } finally {
                    latch.countDown();
                }
            });

            boolean completed = latch.await(10, TimeUnit.SECONDS);

            // Проверка
            assertThat(completed).isTrue();
        }

        // Проверка
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
