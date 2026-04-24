package com.clinic.billing.dto.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpecializationRequest {

    private String name;
    private String status;
}