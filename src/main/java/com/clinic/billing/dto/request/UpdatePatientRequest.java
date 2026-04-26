package com.clinic.billing.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequest {

    private String phone;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String address;
}
