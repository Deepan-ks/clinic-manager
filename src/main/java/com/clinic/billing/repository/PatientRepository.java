package com.clinic.billing.repository;

import com.clinic.billing.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("""
        SELECT p FROM Patient p
        WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR p.phone LIKE CONCAT('%', :query, '%')
    """)
    List<Patient> searchPatients(@Param("query") String query);
}
