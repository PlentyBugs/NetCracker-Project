package org.netcracker.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    /**
     * Метод, который асинхронно посылает письмо с сообщением на указанный email
     * @param emailTo Email-получатель
     * @param subject Тема сообщения
     * @param message Контент сообщения
     */
    @Async
    public void send(String emailTo, String subject, String message) {
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(username);
        smm.setTo(emailTo);
        smm.setSubject(subject);
        smm.setText(message);

        mailSender.send(smm);
    }
}
