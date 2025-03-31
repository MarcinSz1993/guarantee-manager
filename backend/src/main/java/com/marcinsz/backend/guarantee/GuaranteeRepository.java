package com.marcinsz.backend.guarantee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuaranteeRepository extends JpaRepository<Guarantee,Long> {

    List<Guarantee> findAllByUserIdAndKindOfProduct(Long user_id, Product kindOfProduct);

    Page<Guarantee> findByUser_IdAndGuaranteeStatusNot(Long userId, GuaranteeStatus guaranteeStatus, Pageable pageable);

    List<Guarantee> findAllByEndDateBetweenAndSentExpirationMessageFalse(LocalDate startDate, LocalDate endDate);

    Page<Guarantee> findAllByUser_IdAndEndDateBetweenAndSentExpirationMessageFalse(Long userId,LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("""
        SELECT guarantee.kindOfProduct FROM Guarantee guarantee
        WHERE guarantee.user.id = :userId
        GROUP BY guarantee.kindOfProduct
        ORDER BY COUNT (guarantee.id)
        DESC
        LIMIT 1
""")
    Optional<String> findOftenestKindOfProductBoughtByUserId(Long userId);

    @Query(value = """
    SELECT AVG(guarantee.end_date - guarantee.start_date)
    AS avg_guarantee_duration
    FROM Guarantee guarantee
    WHERE guarantee.user_id = :userId
    AND guarantee.kind_of_product = :kindOfProduct
    
""",nativeQuery = true)
    Double getAverageDurationOfGuarantee(@Param("kindOfProduct") String kindOfProduct, @Param("userId") Long userId);
}