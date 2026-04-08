package com.klu.sdp36BE.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetEmail(String to, String token) {
        String resetLink = "http://localhost:3001/reset-password?token=" + token;
        String content = "<h1>Password Reset</h1>" +
                "<p>Click the link below to reset your password. It expires in 15 minutes.</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("arundluffy1410@gmail.com");
            helper.setTo(to);
            helper.setSubject("HealthWell - Password Reset Request");
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
