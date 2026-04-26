package com.clinic.billing.controller;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.service.MedicalServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    public ResponseEntity<List<MedicalServiceResponse>> getServices(
            @RequestParam(required = false) Long specializationId) {

        if (specializationId != null) {
            return ResponseEntity.ok(medicalServiceService.findBySpecializationById(specializationId));
        }
        return ResponseEntity.ok(medicalServiceService.getAllActiveServices());
    }

    @PostMapping
    public ResponseEntity<MedicalServiceResponse> createMedicalService(
            @RequestBody @Valid MedicalServiceRequest medicalServiceRequest) {
        MedicalServiceResponse response = medicalServiceService.createMedicalService(medicalServiceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalServiceResponse> updateMedicalService(@PathVariable Long id,
            @RequestBody @Valid MedicalServiceRequest medicalServiceRequest) {
        MedicalServiceResponse response = medicalServiceService.updateMedicalService(id, medicalServiceRequest);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicalService(@PathVariable Long id) {
        medicalServiceService.deleteMedicalService(id);
        return ResponseEntity.ok().body("Medical Service has been deleted");
    }

}
