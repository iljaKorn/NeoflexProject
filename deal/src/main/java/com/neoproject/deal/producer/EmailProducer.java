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

    @Value("${spring.kafka.topics.statement-denied}")
    private String statementDeniedTopic;

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public void produceMessageForFinishRegistration(String email, UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.FINISH_REGISTRATION);
        emailMessage.setStatementId(statementId);

        kafkaTemplate.send(finishRegistrationTopic, emailMessage);
        log.info("Отправлено сообщение с данными для завершения регистрации: {}", emailMessage);
    }

    public void produceMessageForCreateDocument(String email, UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.CREATE_DOCUMENTS);
        emailMessage.setStatementId(statementId);

        kafkaTemplate.send(createDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для создания документов: {}", emailMessage);

    }

    public void produceMessageForSendDocuments(String email, UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.SEND_DOCUMENTS);
        emailMessage.setStatementId(statementId);

        kafkaTemplate.send(sendDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для отправки документов: {}", emailMessage);
    }

    public void produceMessageForRequestToSign(String email, UUID statementId, String ses_code) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.SEND_SES);
        emailMessage.setStatementId(statementId);
        emailMessage.setText(ses_code);

        kafkaTemplate.send(requestToSignDocumentTopic, emailMessage);
        log.info("Отправлено сообщение с данными для запроса подписи документов: {}", emailMessage);
    }

    public void produceMessageForSignDocuments(String email, UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.CREDIT_ISSUED);
        emailMessage.setStatementId(statementId);

        kafkaTemplate.send(creditIssuedTopic, emailMessage);
        log.info("Отправлено сообщение с данными для подписи документов: {}", emailMessage);

    }

    public void produceMessageForRejectStatement(String email, UUID statementId) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setAddress(email);
        emailMessage.setTheme(Theme.STATEMENT_DENIED);
        emailMessage.setStatementId(statementId);

        kafkaTemplate.send(statementDeniedTopic, emailMessage);
        log.info("Отправлено сообщение с данными для отмены заявки: {}", emailMessage);

    }
}
