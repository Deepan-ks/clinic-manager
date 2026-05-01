package com.clinic.billing.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponse {
    private Long doctorId;
    private String doctorName;
    private String phone;
    private String status;
    private Long specializationId;
    private String specializationName;
}