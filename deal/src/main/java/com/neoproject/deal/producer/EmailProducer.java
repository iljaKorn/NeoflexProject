package com.neoproject.deal.producer;

import com.neoproject.deal.model.dto.EmailMessage;
import com.neoproject.deal.model.enums.Theme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProducer {

    @Value("${spring.kafka.topics.finish-registration}")
    private String finishRegistrationTopic;

    @Value("${spring.kafka.topics.create-documents}")
    private String createDocumentTopic;

    @Value("${spring.kafka.topics.send-documents}")
    private String sendDocumentTopic;

    @Value("${spring.kafka.topics.send-ses}")
    private String requestToSignDocumentTopic;

    @Value("${spring.kafka.topics.credit-issued}")
    private String creditIssuedTopic;

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void produceMessageForFinishRegistration(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.FINISH_REGISTRATION);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Ваша заявка предварительно одобрена, завершите оформление");

        kafkaTemplate.send(finishRegistrationTopic, emailMessage);
        log.info("Отправлено сообщение с данными для завершения регистрации: {}", emailMessage);
    }

    public void produceMessageForCreateDocument(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.CREATE_DOCUMENTS);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Перейти к оформлению документов");

        kafkaTemplate.send(createDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для создания документов: {}", emailMessage);

    }

    public void produceMessageForSendDocuments(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.SEND_DOCUMENTS);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Подписать документы");

        kafkaTemplate.send(sendDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для отправки документов: {}", emailMessage);
    }

    public void produceMessageForRequestToSign(UUID statementId, String ses_code) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.SEND_SES);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Код для подписи: " + ses_code);

        kafkaTemplate.send(requestToSignDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для запроса подписи документов: {}", emailMessage);
    }

    public void produceMessageForSignDocuments(UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress("kornilov.ilja@rambler.ru");
        emailMessage.setTheme(Theme.CREDIT_ISSUED);
        emailMessage.setStatementId(statementId);
        emailMessage.setText("Кредит одобрен");

        kafkaTemplate.send(creditIssuedTopic, emailMessage);
        log.info("Отправлено сообщение с данными для подписи документов: {}", emailMessage);

    }
}
