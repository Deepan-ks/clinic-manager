package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.service.BillingService;
import com.clinic.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;
    private final InvoiceService invoiceService;

    @PostMapping("/bills")
    public ResponseEntity<BillResponse> generateBill(@RequestBody @Valid CreateBillRequest createBillRequest) {
        BillResponse bill = billingService.createBill(createBillRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @GetMapping("/bills/{id}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        BillResponse bill = billingService.getBillById(id);
        return ResponseEntity.ok().body(bill);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<BillResponse>> getAllBills() {
        List<BillResponse> bills = billingService.getAllBills();
        return ResponseEntity.ok().body(bills);
    }

    @PatchMapping("/bills/{id}/cancel")
    public ResponseEntity<String> cancelBill(@PathVariable Long id, @RequestBody @Valid CancelBillRequest cancelBillRequest) {
        billingService.cancelBill(id, cancelBillRequest);
        return ResponseEntity.ok().body("Bill has been cancelled");
    }

    @GetMapping("bills/{id}/invoice")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long id) throws IOException {

        byte[] pdf = invoiceService.generateInvoice(id);

        BillResponse bill = billingService.getBillById(id);

        String safePatientName = bill.getPatientName().replaceAll("[^a-zA-Z0-9]", "_");

        String fileName = safePatientName + "_" + bill.getBillNumber() + ".pdf";

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
