package org.netcracker.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
