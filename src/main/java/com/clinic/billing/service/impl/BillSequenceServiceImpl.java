package com.clinic.billing.service.impl;

import com.clinic.billing.entity.BillSequence;
import com.clinic.billing.repository.BillSequenceRepository;
import com.clinic.billing.service.BillSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillSequenceServiceImpl implements BillSequenceService {

    private final BillSequenceRepository billSequenceRepository;
    private final ApplicationContext applicationContext;

    @Override
    @Transactional
    public String generateBillNumber() {
        String monthYear = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 1. Try to fetch with PESSIMISTIC_WRITE lock
        BillSequence sequence = billSequenceRepository.findByMonthYear(monthYear)
                .orElseGet(() -> {
                    // 2. If not found, create it in a separate transaction via Spring proxy
                    applicationContext.getBean(BillSequenceServiceImpl.class).initSequence(monthYear);
                    // 3. Refetch with lock
                    return billSequenceRepository.findByMonthYear(monthYear)
                            .orElseThrow(() -> new RuntimeException("Failed to initialize sequence for " + monthYear));
                });

        // 4. Increment and save
        int nextValue = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(nextValue);
        sequence.setUpdatedTime(LocalDateTime.now());

        billSequenceRepository.save(sequence);

        // 5. Format: MS-COIM-2024-05-000001
        return String.format("MS-COIM-%s-%06d", monthYear, nextValue);
    }

    /**
     * Creates the sequence record for the month if it doesn't exist.
     * Uses REQUIRES_NEW to ensure the record is committed even if the main transaction fails later,
     * and to handle race conditions where another thread might have just created it.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void initSequence(String monthYear) {
        try {
            BillSequence seq = BillSequence.builder()
                    .monthYear(monthYear)
                    .currentValue(0)
                    .createdTime(LocalDateTime.now())
                    .updatedTime(LocalDateTime.now())
                    .build();
            billSequenceRepository.saveAndFlush(seq);
            log.info("Initialized new bill sequence for month: {}", monthYear);
        } catch (DataIntegrityViolationException e) {
            // Another thread already created it, which is fine
            log.debug("Bill sequence for {} already initialized by another thread", monthYear);
        }
    }
}
