package com.clinic.billing.repository;

import com.clinic.billing.entity.BillSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface BillSequenceRepository extends JpaRepository<BillSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BillSequence> findByMonthYear(String monthYear);
}
