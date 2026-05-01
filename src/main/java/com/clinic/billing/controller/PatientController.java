package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@RequestBody @Valid CreatePatientRequest request) {
        PatientResponse createdPatient = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        PatientResponse patient = patientService.getPatient(id);
        return ResponseEntity.ok().body(patient);
    }

    /**
     * GET /api/v1/patients?search=&page=0&size=20
     * Returns a Page<PatientResponse> when page/size params are present,
     * falls back to the full list otherwise (backward-compatible).
     */
    @GetMapping
    public ResponseEntity<?> getAllPatients(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        if (page != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<PatientResponse> result = patientService.getPatients(search, pageable);
            return ResponseEntity.ok(result);
        }
        // Legacy: no page param → return full list (used by billing autocomplete)
        List<PatientResponse> patientList = patientService.getAllPatients();
        return ResponseEntity.ok(patientList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatient(@RequestParam String query) {
        List<PatientResponse> patientList = patientService.searchPatient(query);
        return ResponseEntity.ok().body(patientList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id,
            @RequestBody @Valid UpdatePatientRequest request) {
        PatientResponse updatedPatient = patientService.updatePatient(id, request);
        return ResponseEntity.ok().body(updatedPatient);
    }
}
