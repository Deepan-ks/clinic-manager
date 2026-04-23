package com.clinic.billing.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientRequest {

    private String name;
    private String phone;
    private Integer age;
    private String gender;
    private String email;
    private String address;
}