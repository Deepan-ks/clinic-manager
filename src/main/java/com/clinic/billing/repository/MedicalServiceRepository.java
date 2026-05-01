package com.clinic.billing.repository;

import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {

    List<MedicalService> findByStatus(Status status);

    List<MedicalService> findBySpecializationId(Long specializationId);

    /** Paginated — search by name; optionally filter by specialization */
    @Query("""
        SELECT s FROM MedicalService s
        WHERE (:search = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:#{#specializationId == null} = true OR s.specialization.id = :specializationId)
        ORDER BY s.name ASC
    """)
    Page<MedicalService> findAllPaged(
            @Param("search") String search,
            @Param("specializationId") Long specializationId,
            Pageable pageable);
}
