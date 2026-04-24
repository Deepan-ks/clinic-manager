package com.clinic.billing.repository;

import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    List<MedicalService> findByStatus(Status status);
    List<MedicalService> findBySpecializationId(Long specializationId);
}
