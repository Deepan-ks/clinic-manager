package com.clinic.billing.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientResponse {
    private Long patientId;
    private String patientName;
    private int age;
    private String gender;
    private String patientPhone;
}
