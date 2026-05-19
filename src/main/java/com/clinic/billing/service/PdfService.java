package com.clinic.billing.service;

import com.clinic.billing.entity.Bill;
import com.itextpdf.layout.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PdfService {

    ByteArrayOutputStream buildPdf(Bill bill) throws IOException;
}
