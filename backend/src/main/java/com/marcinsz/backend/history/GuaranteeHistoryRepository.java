package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuaranteeHistoryRepository extends JpaRepository<GuaranteeHistory,Long> {

    @Query("""
 SELECT COUNT (guaranteeHistory)
 FROM GuaranteeHistory guaranteeHistory
 WHERE guaranteeHistory.positiveFeedback = :positiveFeedback
 AND guaranteeHistory.user.id = :userId
""")
    Integer countByPositiveFeedbackAndUserId(@Param("positiveFeedback") Boolean positiveFeedback,
                                             @Param("userId") Long userId);

    Integer countAllByStatusAndUserId(GuaranteeStatus guaranteeStatus, Long userId);
}
