package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        List<PatientResponse> patientList = patientService.getAllPatients();
        return ResponseEntity.ok().body(patientList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientResponse>> searchPatient(@RequestParam String query) {
        List<PatientResponse> patientList = patientService.searchPatient(query);
        return ResponseEntity.ok().body(patientList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @RequestBody @Valid UpdatePatientRequest request) {
        PatientResponse updatedPatient = patientService.updatePatient(id, request);
        return ResponseEntity.ok().body(updatedPatient);
    }


}
