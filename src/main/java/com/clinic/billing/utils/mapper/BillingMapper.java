package com.clinic.billing.utils.mapper;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillItemResponse;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.*;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class BillingMapper {

    public BillItem createBillItemEntity(MedicalService service, BillItemRequest billItemRequest, BigDecimal lineTotal) {
        // implement safe normalizer which checks for null, empty, and negative values for any objects - add it as util
        return BillItem.builder()
                .service(service)
                .serviceName(service.getName())
                .quantity(billItemRequest.getQuantity())
                .lineTotal(lineTotal)
                .build();
    }

    public Bill createBillEntity(String billNumber, Patient patient, Doctor doctor,
                                 Specialization specialization, CreateBillRequest request,
                                 BigDecimal subtotal, BigDecimal discountValue, BigDecimal discountPercent, BigDecimal total,
                                 PaymentMode paymentMode, List<BillItem> billItems) {
            return Bill.builder()
                    .billNumber(billNumber)
                    .patient(patient)
                    .doctor(doctor)
                    .specialization(specialization)
                    .doctorName(request.getDoctorName())
                    .subtotal(subtotal)
                    .discountAmount(discountValue)
                    .discountPercent(discountPercent)
                    .grandTotal(total)
                    .paymentMode(paymentMode)
                    .status(BillStatus.ACTIVE)
                    .items(billItems)
                    .createdTime(java.time.LocalDateTime.now())
                    .updatedTime(java.time.LocalDateTime.now())
                    .build();
    }

    public BillResponse createBillResponse(Bill bill) {

        List<BillItemResponse> itemResponses = prepareBillItemResponse(bill);

        return BillResponse.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .patientName(bill.getPatient().getName())
                .patientPhone(bill.getPatient().getPhone())
                .patientAddress(bill.getPatient().getAddress())
                .doctorName(bill.getDoctorName())
                .subtotal(bill.getSubtotal())
                .grandTotal(bill.getGrandTotal())
                .discountAmount(bill.getDiscountAmount())
                .discountPercent(bill.getDiscountPercent())
                .paymentMode(bill.getPaymentMode().name())
                .status(bill.getStatus().name())
                .notes(bill.getNotes())
                .items(itemResponses)
                .createdTime(bill.getCreatedTime())
                .build();
    }

    private List<BillItemResponse> prepareBillItemResponse(Bill bill) {
        return createBillItemResponses(bill.getItems());
    }

    public List<BillItemResponse> createBillItemResponses(List<BillItem> billItems) {
        List<BillItemResponse> itemResponses = new ArrayList<>();
        for (BillItem item : billItems) {
            BillItemResponse response = BillItemResponse.builder()
                    .serviceName(item.getServiceName())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .lineTotal(item.getLineTotal())
                    .build();
            itemResponses.add(response);
        }
        return itemResponses;
    }
}
