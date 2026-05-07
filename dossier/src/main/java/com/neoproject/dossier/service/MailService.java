package com.neoproject.dossier.service;

import com.neoproject.dossier.model.dto.EmailMessage;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Сервис отправки писем на почту пользователя
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String emailAddressFrom;

    private final JavaMailSender mailSender;

    /**
     * Метод для отправки сообщения на почту пользователя для завершения регистрации
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void finishRegistration(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Завершение оформления");
        messageToEmail.setText("Ваша заявка предварительно одобрена, завершите оформление");

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
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

            String urlForSendDocuments = "http://localhost:8081/deal/document/" +
                    message.getStatementId() + "/send";

            String htmlText = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                            </head>
                            <body>
                                <div>
                                    <h2>Оформление кредитных документов</h2>
                                    <p>Условия кредита выбраны, далее необходимо оформить документы</p>
                            
                                    <p>Для продолжения нажмите на кнопку ниже:</p>
                            
                                    <p>
                                        <a href="%s" class="button">Оформить документы</a>
                                    </p>
                                </div>
                            </body>
                            </html>
                            """,
                    urlForSendDocuments
            );
            helper.setText(htmlText, true);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());

        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new RuntimeException("Ошибка при отправке письма", e);
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

            String urlForSendDocuments = "http://localhost:8081/deal/document/" +
                    message.getStatementId() + "/sign";

            String htmlText = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                            </head>
                            <body>
                                <div>
                                    <h2>Подписание кредитных документов</h2>
                                    <p>Документы сформированы и готовы к подписанию</p>
                            
                                    <p>Для продолжения нажмите на кнопку ниже:</p>
                            
                                    <p>
                                        <a href="%s" class="button">Подписать документы</a>
                                    </p>
                                </div>
                            </body>
                            </html>
                            """,
                    urlForSendDocuments
            );
            helper.setText(htmlText, true);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());

        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new RuntimeException("Ошибка при отправке письма", e);
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

            String urlForSendDocuments = "http://localhost:8081/deal/document/" +
                    message.getStatementId() + "/code"
                    + "?code=" + message.getText();

            String htmlText = String.format("""
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                            </head>
                            <body>
                                <div>
                                    <h2>Подтверждение подписания</h2>
                                    <p>Код для подписания документов: %s</p>
                            
                                    <p>Для подтверждения введите полученный код и нажмите на кнопку ниже:</p>
                            
                                    <p>
                                        <a href="%s" class="button">Подтвердить подписание</a>
                                    </p>
                                </div>
                            </body>
                            </html>
                            """,
                    message.getText(),
                    urlForSendDocuments
            );
            helper.setText(htmlText, true);

            mailSender.send(mimeMessage);
            log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());

        } catch (Exception e) {
            log.error("Ошибка при отправке письма на {}", message.getAddress(), e);
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }

    /**
     * Метод для отправки сообщения на почту пользователя для подписи документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void signDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Выдача кредита");
        messageToEmail.setText(String.format("Кредит с номером: %s одобрен", message.getStatementId()));

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
    }

    /**
     * Метод для отправки сообщения на почту пользователя для отмены заявки
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void deniedStatement(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Отмена заявки");
        messageToEmail.setText("Заявка отменена");

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}", message.getAddress(), message.getTheme());
    }
}
