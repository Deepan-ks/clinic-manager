package com.clinic.billing.repository;

import com.clinic.billing.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface BillRepository extends JpaRepository<Bill, Long> {

    /** Paginated — search by patient name/phone; optional date range */
    @Query("""
        SELECT b FROM Bill b
        WHERE (:search = '' OR LOWER(b.patient.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR b.patient.phone LIKE CONCAT('%', :search, '%'))
          AND (b.createdTime >= :from)
          AND (b.createdTime <= :to)
        ORDER BY b.createdTime DESC
    """)
    Page<Bill> findAllPaged(
            @Param("search") String search,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
