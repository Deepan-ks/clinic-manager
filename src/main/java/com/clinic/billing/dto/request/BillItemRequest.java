package com.clinic.billing.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

    private Long serviceId;
    private Integer quantity;
}