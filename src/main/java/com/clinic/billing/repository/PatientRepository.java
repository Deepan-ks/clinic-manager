package com.clinic.billing.repository;

import com.clinic.billing.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    /** Used by billing autocomplete — returns raw list */
    @Query("""
        SELECT p FROM Patient p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR p.phone LIKE CONCAT('%', :query, '%')
    """)
    List<Patient> searchPatients(@Param("query") String query);

    /** Paginated list — search is optional (pass empty string to skip) */
    @Query("""
        SELECT p FROM Patient p
        WHERE (:search = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                            OR p.phone LIKE CONCAT('%', :search, '%'))
        ORDER BY p.createdTime DESC
    """)
    Page<Patient> findAllPaged(@Param("search") String search, Pageable pageable);
}
