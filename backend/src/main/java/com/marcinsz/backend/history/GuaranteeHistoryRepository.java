package com.marcinsz.backend.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuaranteeHistoryRepository extends JpaRepository<GuaranteeHistory,Long> {
}
