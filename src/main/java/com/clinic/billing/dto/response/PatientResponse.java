package com.clinic.billing.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private Long patientId;
    private String patientName;
    private int age;
    private String gender;
    private String patientPhone;
}