package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.service.SpecializationService;
import com.clinic.billing.service.SpecializationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService service;

    @PostMapping("/specializations")
    public ResponseEntity<SpecializationResponse> createSpecialization(@RequestBody CreateSpecializationRequest request) {
        SpecializationResponse response = service.createSpecialization(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<SpecializationResponse>> fetchAllSpecialization() {
        List<SpecializationResponse> response = service.getAllSpecialization();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/specializations/{id}")
    public ResponseEntity<SpecializationResponse> fetchBySpecializationId(@PathVariable Long id) {
        SpecializationResponse response = service.getBySpecializationId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/specializations/{id}")
    public ResponseEntity<SpecializationResponse> updateSpecialization(@PathVariable Long id,
                                         @RequestBody CreateSpecializationRequest request) {
        SpecializationResponse response = service.updateSpecialization(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/specializations/{id}")
    public ResponseEntity<String> deleteSpecialization(@PathVariable Long id) {
        service.deleteSpecialization(id);
        return ResponseEntity.ok("Specialization has been deleted");
    }

}
