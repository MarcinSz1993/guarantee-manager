package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuaranteeHistoryService {
    private final GuaranteeHistoryRepository guaranteeHistoryRepository;

    public Integer getAllByGuaranteeStatus(GuaranteeStatus guaranteeStatus, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
        return guaranteeHistoryRepository.countAllByStatusAndUserId(guaranteeStatus, user.getId());
    }

    public Integer getAllFeedbacks(Boolean positiveFeedback, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
        return guaranteeHistoryRepository
                .countByPositiveFeedbackAndUserId(positiveFeedback,user.getId());
    }
}
