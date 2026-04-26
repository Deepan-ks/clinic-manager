package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.service.DoctorService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> createDoctor(@RequestBody @Valid CreateDoctorRequest request) {
        DoctorResponse response = doctorService.createDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> fetchAllSpecializedDoctors(
            @RequestParam(required = false) Long specializationId) {
        List<DoctorResponse> response = doctorService.getAllDoctorBySpecialization(specializationId);
        return ResponseEntity.ok(response);
    }

    // @GetMapping
    // public ResponseEntity<List<DoctorResponse>> fetchAllDoctors(){
    // List<DoctorResponse> response = doctorService.findAllDoctors();
    // return ResponseEntity.ok(response);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> fetchByDoctorId(@PathVariable Long id) {
        DoctorResponse response = doctorService.getByDoctorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable Long id,
            @RequestBody @Valid CreateDoctorRequest request) {
        DoctorResponse response = doctorService.updateDoctor(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor has been deleted");
    }
}
