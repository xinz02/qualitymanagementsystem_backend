package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("{spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token, boolean newUser) {
        try {
            String resetLink = frontendUrl + "/resetpassword?token=" + token;
            String subject = "Password Reset Request";
            String content = buildEmailContent_ForgotPassword(resetLink);
            if (newUser) {
                content = buildEmailContent_NewUser(resetLink);
            }


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildEmailContent_ForgotPassword(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; }" +
                "  .button { background-color: #4CAF50; border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Password Reset Request</h2>" +
                "<p>You requested to reset your password. Click the button below to reset it:</p>" +
                "<a href=\"" + resetLink + "\" class=\"button\">Reset Password</a>" +
                "<p>If you didn't request this, please ignore this email.</p>" +
                "<p>This link will expire in 1 hour.</p>" +
                "</body>" +
                "</html>";
    }

    private String buildEmailContent_NewUser(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; }" +
                "  .button { background-color: #4CAF50; border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 16px; margin: 4px 2px; cursor: pointer; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Set Your Password</h2>" +
                "<p>You account have been created. Click the button below to set your password:</p>" +
                "<a href=\"" + resetLink + "\" class=\"button\">Reset Password</a>" +
                "<p>If you are not in charge in the Quality Management System for FC UTM, please ignore this email.</p>" +
                "<p>This link will expire in 1 hour.</p>" +
                "</body>" +
                "</html>";
    }
}
