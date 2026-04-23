package com.clinic.billing.controller;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.service.MedicalServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class MedicalServiceController {

    private final MedicalServiceService medicalServiceService;

    @GetMapping
    public ResponseEntity<List<MedicalServiceResponse>> getActiveMedicalService(){
        List<MedicalServiceResponse> allActiveServices = medicalServiceService.getAllActiveServices();
        return ResponseEntity.ok().body(allActiveServices);
    }

    @PostMapping
    public ResponseEntity<MedicalServiceResponse> createMedicalService(@RequestBody @Valid MedicalServiceRequest medicalServiceRequest){
        MedicalServiceResponse response = medicalServiceService.createMedicalService(medicalServiceRequest);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalServiceResponse> updateMedicalService(@PathVariable Long id, @RequestBody @Valid MedicalServiceRequest medicalServiceRequest){
        MedicalServiceResponse response = medicalServiceService.updateMedicalService(id, medicalServiceRequest);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicalService(@PathVariable Long id){
        medicalServiceService.deleteMedicalService(id);
        return ResponseEntity.ok().body("Medical Service has been deleted");
    }

}
