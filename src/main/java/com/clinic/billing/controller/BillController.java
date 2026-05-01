package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.service.BillingService;
import com.clinic.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillingService billingService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<BillResponse> generateBill(@RequestBody @Valid CreateBillRequest createBillRequest) {
        BillResponse bill = billingService.createBill(createBillRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        BillResponse bill = billingService.getBillById(id);
        return ResponseEntity.ok(bill);
    }

    /**
     * GET /api/v1/bills?search=&fromDate=2025-01-01&toDate=2025-12-31&page=0&size=20
     * Returns Page<BillResponse> when page param present, full list otherwise.
     */
    @GetMapping
    public ResponseEntity<?> getAllBills(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "20") int size) {

        if (page != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<BillResponse> result = billingService.getBills(search, fromDate, toDate, pageable);
            return ResponseEntity.ok(result);
        }
        List<BillResponse> bills = billingService.getAllBills();
        return ResponseEntity.ok(bills);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBill(@PathVariable Long id,
            @RequestBody @Valid CancelBillRequest cancelBillRequest) {
        billingService.cancelBill(id, cancelBillRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/invoice")
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
