package com.clinic.billing.dto.request;

import lombok.Data;

@Data
public class BillItemRequest {
    private Long serviceId;
    private Integer quantity;
}
