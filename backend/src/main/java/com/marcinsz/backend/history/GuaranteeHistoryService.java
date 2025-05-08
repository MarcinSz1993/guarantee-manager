package com.marcinsz.backend.history;

import com.marcinsz.backend.exception.GuaranteeNotFoundException;
import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeRepository;
import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.kafka.KafkaService;
import com.marcinsz.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GuaranteeHistoryService {
    private final GuaranteeHistoryRepository guaranteeHistoryRepository;
    private final GuaranteeRepository guaranteeRepository;
    private final KafkaService kafkaService;

    public void addGuaranteeChange(Authentication connectedUser, CreateGuaranteeHistoryRequest createGuaranteeHistoryRequest){
        if (createGuaranteeHistoryRequest.getGuaranteeId() == null ||
        createGuaranteeHistoryRequest.getGuaranteeId() == 0){
            throw new GuaranteeNotFoundException("Guarantee id is null");
        }
        User user = (User) connectedUser.getPrincipal();
        Guarantee guarantee = guaranteeRepository.findById(createGuaranteeHistoryRequest.getGuaranteeId()).orElseThrow();
        GuaranteeHistory guaranteeHistory = GuaranteeHistory.builder()
                .guarantee(guarantee)
                .user(user)
                .status(createGuaranteeHistoryRequest.getGuaranteeStatus())
                .changeTime(LocalDateTime.now())
                .notes(createGuaranteeHistoryRequest.getNotes())
                .positiveFeedback(createGuaranteeHistoryRequest.getPositiveFeedback())
                .build();
        guaranteeHistoryRepository.save(guaranteeHistory);
        kafkaService.sendMessage("guarantee-history", createGuaranteeHistoryRequest.getNotes());
    }

    public Integer getAllByGuaranteeStatus(GuaranteeStatus guaranteeStatus, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
        return guaranteeHistoryRepository.countAllByStatusAndUserId(guaranteeStatus, user.getId());
    }

    public Integer getAllFeedbacks(Boolean positiveFeedback, Authentication connectedUser){
        User user = (User) connectedUser.getPrincipal();
        return guaranteeHistoryRepository
                .countByPositiveFeedbackAndUserId(positiveFeedback,user.getId());
    }

    public Page<GuaranteeHistoryResponse> getAllUserGuaranteesHistories(Authentication connectedUser,int pageNumber, int pageSize){
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("changeTime").descending());
        Page<GuaranteeHistory> guaranteeHistories = guaranteeHistoryRepository.findAllByUserId(user.getId(),pageable);
        return guaranteeHistories.map(guaranteeHistory -> GuaranteeHistoryResponse.builder()
                .id(guaranteeHistory.getId())
                .brand(guaranteeHistory.getGuarantee().getBrand())
                .model(guaranteeHistory.getGuarantee().getModel())
                .guaranteeStatus(guaranteeHistory.getStatus())
                .kindOfProduct(guaranteeHistory.getGuarantee().getKindOfProduct())
                .changeTime(guaranteeHistory.getChangeTime())
                .notes(guaranteeHistory.getNotes())
                .positiveFeedback(guaranteeHistory.isPositiveFeedback())
                .build());

    }

    public void deleteGuaranteeHistory(Long guaranteeHistoryId) {
        guaranteeHistoryRepository.deleteById(guaranteeHistoryId);
        //todo zabezpieczyć metodę przez usunięciem nieswojej zmiany gwaracji.
    }
}
