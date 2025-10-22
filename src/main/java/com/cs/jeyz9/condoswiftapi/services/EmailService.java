package com.cs.jeyz9.condoswiftapi.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendHtmlEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

//    public void sendVerificationEmail(String toEmail, String token) {
//        String subject = "Account Verification - JBlog";
//        String verificationUrl = "https://yourdomain.com/api/verify?token=" + token;
//        String body = """
//                Dear user,
//                
//                Thank you for registering with JBlog.
//                Please verify your email address by clicking the link below:
//                
//                %s
//                
//                This link will expire in 24 hours.
//                
//                Regards,
//                The JBlog Team
//                """.formatted(verificationUrl);
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//
//        mailSender.send(message);
//    }
}

