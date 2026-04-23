package com.clinic.billing.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationResponse {

    private Long specializationId;
    private String specializationName;
}