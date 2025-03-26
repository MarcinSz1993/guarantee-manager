package com.marcinsz.backend.notification.dashboard;

import com.marcinsz.backend.exception.UserNotificationPreferenceException;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;
import com.marcinsz.backend.guarantee.GuaranteeResponse;
import com.marcinsz.backend.mapper.GuaranteeMapper;
import com.marcinsz.backend.notification.NotificationPreference;
import com.marcinsz.backend.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardNotificationService {
    private final GuaranteeRepository guaranteeRepository;

    @Transactional
    public Page<GuaranteeResponse> fetchGuaranteesExpiresIn7Days(Authentication connectedUser,
                                                                 int pageNumber,
                                                                 int pageSize) {
        User user = (User) connectedUser.getPrincipal();
        if (user.getNotificationPreference() != NotificationPreference.DASHBOARD &&
                user.getNotificationPreference() != NotificationPreference.ALL) {
            log.info("User {} does not have dashboard notifications enabled.", user.getUsername());
            throw new UserNotificationPreferenceException("User must have DASHBOARD or ALL as notification preference.");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("endDate").descending());
        Page<Guarantee> guaranteesExpiresIn7Days = guaranteeRepository.findAllByUser_IdAndEndDateBetweenAndSentExpirationMessageFalse(user.getId(), LocalDate.now(), LocalDate.now().plusDays(7), pageable);
        log.info("Found {} guarantees with specified criteria.", guaranteesExpiresIn7Days.getTotalElements());
        guaranteesExpiresIn7Days.forEach(guarantee -> {
            guarantee.setSentExpirationMessage(true);
            guaranteeRepository.save(guarantee);
        });
        return guaranteesExpiresIn7Days.map(GuaranteeMapper::mapGuaranteeToGuaranteeResponse);
    }
}
