package com.clinic.billing.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorRequest {

    private String name;
    private Long specializationId;
    private String phone;
    private String status;
}