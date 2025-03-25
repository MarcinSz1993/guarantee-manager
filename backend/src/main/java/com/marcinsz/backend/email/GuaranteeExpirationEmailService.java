package com.marcinsz.backend.email;

import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuaranteeExpirationEmailService {
    private final GuaranteeRepository guaranteeRepository;
    private final NotificationService emailNotificationService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void sendGuaranteeExpirationEmail() {
        log.info("Preparing to send expiration emails...");
        List<Guarantee> guarantees = guaranteeRepository.findAllByEndDateBetweenAndSentExpirationMessageFalse(LocalDate.now(), LocalDate.now().plusDays(7));

        if (guarantees.isEmpty()) {
            log.info("No guarantees expiring soon.");
            return;
        }

        guarantees.forEach(guarantee -> {
            try {
                emailNotificationService.sendNotification(
                        guarantee.getUser().getEmail(),
                        guarantee.getUser().getUsername(),
                        EmailTemplateName.GUARANTEE_EXPIRES,
                        guarantee.getBrand(),
                        String.valueOf(ChronoUnit.DAYS.between(LocalDate.now(), guarantee.getEndDate()))
                );

                guarantee.setSentExpirationMessage(true);
                log.info("Sent expiration message to {}", guarantee.getUser().getUsername());

            } catch (MessagingException e) {
                log.error("Failed to send email to {}", guarantee.getUser().getEmail(), e);
            }
        });

        log.info("All expiration emails sent.");
    }
}
