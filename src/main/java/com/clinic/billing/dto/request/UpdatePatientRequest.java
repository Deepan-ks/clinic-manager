package com.clinic.billing.dto.request;

import com.clinic.billing.utils.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePatientRequest {

    @NotBlank(message = Constants.PHONE_REQUIRED)
    @Pattern(regexp = "\\d{10}", message = Constants.INVALID_PHONE_FORMAT)
    private String phone;

    @Email(message = Constants.INVALID_EMAIL)
    private String email;

    private String address;
}
