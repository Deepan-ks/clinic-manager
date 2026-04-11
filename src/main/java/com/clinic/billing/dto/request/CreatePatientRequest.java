package com.clinic.billing.dto.request;

import lombok.Data;

@Data
public class CreatePatientRequest {

    private String name;
    private String phone;
    private Integer age;
    private String gender;
    private String email;
}