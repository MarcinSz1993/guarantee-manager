package com.marcinsz.backend.guarantee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GuaranteeRepository extends JpaRepository<Guarantee,Long> {

    Page<Guarantee> findByUser_IdAndGuaranteeStatusNot(Long userId, GuaranteeStatus guaranteeStatus, Pageable pageable);
    List<Guarantee> findAllByEndDateBetweenAndSentExpirationMessageFalse(LocalDate startDate, LocalDate endDate);
    Page<Guarantee> findAllByUser_IdAndEndDateBetweenAndSentExpirationMessageFalse(Long userId,LocalDate startDate, LocalDate endDate, Pageable pageable);
}