package com.neoproject.dossier.service;

import com.neoproject.dossier.model.dto.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}" , message.getAddress(), message.getTheme());
    }

    /**
     * Метод для отправки сообщения на почту пользователя для создания документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void createDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Создание документов");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}" , message.getAddress(), message.getTheme());
    }

    /**
     * Метод для отправки сообщения на почту пользователя для отправки документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void sendDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Документы на кредит");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}" , message.getAddress(), message.getTheme());
    }

    /**
     * Метод для отправки сообщения на почту пользователя для запроса подписи документов
     *
     * @param message dto с данными для отправки писем на почту
     */
    public void requestToSignDocuments(EmailMessage message) {
        SimpleMailMessage messageToEmail = new SimpleMailMessage();
        messageToEmail.setFrom(emailAddressFrom);
        messageToEmail.setTo(message.getAddress());
        messageToEmail.setSubject("Подписание документов");
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}" , message.getAddress(), message.getTheme());
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
        messageToEmail.setText(message.getText());

        mailSender.send(messageToEmail);
        log.info("Сообщение отправлено на почту {} с темой {}" , message.getAddress(), message.getTheme());
    }
}
