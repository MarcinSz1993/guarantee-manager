package com.marcinsz.backend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailNotificationService implements NotificationService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.application.mailing.frontend.activation-url}")
    private String activationUrl;

    @Override
    @Async
    public void sendNotification(String to, String username, EmailTemplateName emailTemplate, String subject, String message) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        if (emailTemplate == EmailTemplateName.ACTIVATE_ACCOUNT){
            properties.put("confirmationUrl", activationUrl);
            properties.put("activation_code", message);
        } else if (emailTemplate == EmailTemplateName.GUARANTEE_EXPIRES) {
            properties.put("brand", subject);
            properties.put("daysToExpire", message);
        }

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom("marcinsz1993@hotmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String template = templateEngine.process(emailTemplate.getName().toLowerCase(), context);
        helper.setText(template, true);

        javaMailSender.send(mimeMessage);
    }
}
