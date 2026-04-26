package com.clinic.billing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientRequest {

    @NotBlank(message = Constants.NAME_REQUIRED)
    private String name;

    @NotBlank(message = Constants.PHONE_REQUIRED)
    @Pattern(regexp = "\\d{10}", message = Constants.INVALID_PHONE_FORMAT)
    private String phone;

    @Min(value = 0, message = Constants.AGE_NEGATIVE)
    private Integer age;

    @NotBlank(message = Constants.GENDER_REQUIRED)
    private String gender;

    @Email(message = Constants.INVALID_EMAIL)
    private String email;

    private String address;
}