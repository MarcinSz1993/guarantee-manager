package com.marcinsz.backend.notification.email;

import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;
import com.marcinsz.backend.notification.NotificationService;
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
        log.info("Found {} guarantees", guarantees.size());
        List<Guarantee> guaranteesToNotify = guarantees.stream()
                .filter(guarantee -> guarantee.getUser().getNotificationPreference() != null)
                .filter(guarantee -> guarantee.getUser().getNotificationPreference().isEmailNotificationEnabled())
                .toList();
        log.info("Guarantees after filtering: {}", guaranteesToNotify.size());
        if (guaranteesToNotify.isEmpty()) {
            log.info("No guarantees expiring soon.");
            return;
        }
        sendEmails(guaranteesToNotify);
        log.info("All expiration emails sent.");
    }

    private void sendEmails(List<Guarantee> filteredGuarantees) {
        filteredGuarantees.forEach(filteredGuarantee -> {
            try {
                emailNotificationService.sendNotification(
                        filteredGuarantee.getUser().getEmail(),
                        filteredGuarantee.getUser().getUsername(),
                        EmailTemplateName.GUARANTEE_EXPIRES,
                        filteredGuarantee.getBrand(),
                        String.valueOf(ChronoUnit.DAYS.between(LocalDate.now(), filteredGuarantee.getEndDate()))
                );

                filteredGuarantee.setSentExpirationMessage(true);
                log.info("Sent expiration message to {}", filteredGuarantee.getUser().getUsername());

            } catch (MessagingException e) {
                log.error("Failed to send email to {}", filteredGuarantee.getUser().getEmail(), e);
            }
        });
    }
}
