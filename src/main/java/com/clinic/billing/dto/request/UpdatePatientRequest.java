package com.clinic.billing.dto.request;

import lombok.Data;

@Data
public class UpdatePatientRequest {
    private String phone;
    private String email;
}
