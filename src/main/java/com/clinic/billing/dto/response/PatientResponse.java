package com.clinic.billing.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long patientId;
    private String patientName;
    private int age;
    private String address;
    private String gender;
    private String patientPhone;
    private String email;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}