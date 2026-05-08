package com.neoproject.dossier.service;

import com.neoproject.dossier.exception.DossierEmailSendException;
import com.neoproject.dossier.model.dto.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

/**
 * Сервис отправки писем на почту пользователя
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String emailAddressFrom;

    @Value("${app.email.base-url}")
    private String baseUrl;

    @Value("${app.email.paths.send-documents}")
    private String sendDocumentPath;

    @Value("${app.email.paths.sign-documents}")
    private String signDocumentPath;

    @Value("${app.email.paths.confirm-sign}")
    private String confirmSignPath;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final DocumentGenerationService documentGenerationService;

    /**
     * Метод для отправки сообщения на почту пользователя для завершения регистрации
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void finishRegistration(EmailMessage message) {
        try {
            SimpleMailMessage messageToEmail = new SimpleMailMessage();
            messageToEmail.setFrom(emailAddressFrom);
            messageToEmail.setTo(message.getAddress());
            messageToEmail.setSubject("Завершение оформления");
            messageToEmail.setText("Ваша заявка предварительно одобрена, завершите оформление");

            mailSender.send(messageToEmail);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для создания документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void createDocuments(EmailMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailAddressFrom);
            helper.setTo(message.getAddress());
            helper.setSubject("Создание документов");

            Context context = new Context();
            String urlForSendDocuments = baseUrl +
                    sendDocumentPath.replace("{statementId}", message.getStatementId().toString());
            context.setVariable("link", urlForSendDocuments);

            String htmlText = templateEngine.process("emails/emailForCreateDocument.html", context);

            helper.setText(htmlText, true);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());

        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для отправки документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void sendDocuments(EmailMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailAddressFrom);
            helper.setTo(message.getAddress());
            helper.setSubject("Подписание документов");

            File document = documentGenerationService.generateDocument(message.getStatementId());

            Context context = new Context();
            String urlForSendDocuments = baseUrl +
                    signDocumentPath.replace("{statementId}", message.getStatementId().toString());
            context.setVariable("link", urlForSendDocuments);

            String htmlText = templateEngine.process("emails/emailForSendDocument.html", context);

            helper.setText(htmlText, true);

            FileSystemResource file = new FileSystemResource(document);
            helper.addAttachment(document.getName(), file);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
            document.delete();
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для запроса подписи документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void requestToSignDocuments(EmailMessage message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emailAddressFrom);
            helper.setTo(message.getAddress());
            helper.setSubject("Подтверждение подписания документов");

            Context context = new Context();
            String urlForSendDocuments = baseUrl +
                    confirmSignPath.replace("{statementId}", message.getStatementId().toString()) +
                    "?code=" + message.getText();
            context.setVariable("link", urlForSendDocuments);
            context.setVariable("code", message.getText());

            String htmlText = templateEngine.process("emails/emailForRequestToSign.html", context);

            helper.setText(htmlText, true);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());

        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для подписи документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void signDocuments(EmailMessage message) {
        try {
            SimpleMailMessage messageToEmail = new SimpleMailMessage();
            messageToEmail.setFrom(emailAddressFrom);
            messageToEmail.setTo(message.getAddress());
            messageToEmail.setSubject("Выдача кредита");
            messageToEmail.setText(String.format("Кредит с номером: %s одобрен", message.getStatementId()));

            mailSender.send(messageToEmail);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для отмены заявки
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void deniedStatement(EmailMessage message) {
        try {
            SimpleMailMessage messageToEmail = new SimpleMailMessage();
            messageToEmail.setFrom(emailAddressFrom);
            messageToEmail.setTo(message.getAddress());
            messageToEmail.setSubject("Отмена заявки");
            messageToEmail.setText("Заявка отменена");

            mailSender.send(messageToEmail);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new DossierEmailSendException("Ошибка при отправке письма");
        }
    }
}
