package com.clinic.billing.service;

import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.BillItem;
import com.clinic.billing.repository.BillRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final BillRepository billRepository;

    @Override
    public byte[] generateInvoice(Long billId) throws IOException {

        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // =========================
        // CLINIC HEADER (CLEAN STYLE)
        // =========================

        document.add(space(1));

        document.add(new Paragraph("MEDISMILE HEALTH CARE")
                .setBold()
                .setFontSize(22)
                .setTextAlignment(TextAlignment.LEFT));

        // underline
        LineSeparator headerLine = new LineSeparator(new SolidLine());
        headerLine.setWidth(UnitValue.createPercentValue(80));
        headerLine.setHorizontalAlignment(HorizontalAlignment.LEFT);
        document.add(headerLine);

        // address
        document.add(new Paragraph("19/A, Rani Gardens, SIHS colony road, Singanallur, Coimbatore 641005 \n" +
                "Email: medismilehc@gmail.com     Phone: +91 9342639317")
                .setFontSize(12));

        document.add(space(2));


        // =========================
        // BILL INFO
        // =========================

        Table info = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        info.addCell(infoCell("BILL TO",
                "Patient Name: " + bill.getPatient().getName(),
                "Mobile: " + bill.getPatient().getPhone()));

        info.addCell(infoCell("INVOICE",
                "Invoice No: " + bill.getBillNumber(),
                "Date: " + bill.getCreatedTime().toLocalDate()));

        document.add(info);
        document.add(space(1));

        // =========================
        // MAIN TABLE
        // =========================

        float[] cols = {1, 3, 5, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(cols))
                .useAllAvailableWidth();

        addHeaders(table);

        int i = 1;
        for (BillItem item : bill.getItems()) {
            table.addCell(bodyCell(String.valueOf(i++)));
            table.addCell(bodyCell(item.getServiceName()));
            table.addCell(bodyCell("Dental consultation to evaluate concerns\nand discuss treatment options."));
            table.addCell(rightCell("₹ " + item.getUnitPrice()));
            table.addCell(rightCell(String.valueOf(item.getQuantity())));
            table.addCell(rightCell("₹ " + item.getLineTotal()));
        }

        // SUMMARY INSIDE TABLE
        addSummary(table, bill);

        document.add(table);

        // =========================
        // SIGNATURE
        // =========================

        document.add(space(3));

        LineSeparator line = new LineSeparator(new SolidLine());
        line.setWidth(UnitValue.createPercentValue(30));
        line.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        document.add(line);

        document.add(new Paragraph("Authorized Signature")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));

        document.close();
        return out.toByteArray();
    }

    private Image loadLogo() throws IOException {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("static/images/Medismile Logo.jpg");

        return new Image(ImageDataFactory.create(is.readAllBytes()))
                .scaleToFit(80, 80);
    }

    private Cell infoCell(String title, String line1, String line2) {
        return new Cell()
                .add(new Paragraph(title).setBold().setFontSize(11))
                .add(new Paragraph(line1).setFontSize(10))
                .add(new Paragraph(line2).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }

    private void addHeaders(Table table) {
        String[] headers = {"Sl No", "Service", "Description", "Price", "Qty", "Total"};

        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFontSize(10))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5));
        }
    }

    private Cell bodyCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setPadding(4)
                .setBorderBottom(new SolidBorder(0.5f));
    }

    private Cell rightCell(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(4)
                .setBorderBottom(new SolidBorder(0.5f));
    }

    private void addSummary(Table table, Bill bill) {

        table.addCell(empty(4));
        table.addCell(summaryLabel("Sub Total"));
        table.addCell(summaryValue("₹ " + bill.getSubtotal()));

        table.addCell(empty(4));
        table.addCell(summaryLabel("Discount (₹)"));
        table.addCell(summaryValue("₹ " + bill.getDiscountAmount()));

        table.addCell(empty(4));
        table.addCell(summaryLabel("Discount (%)"));
        table.addCell(summaryValue("% " +  bill.getDiscountPercent()));

        table.addCell(empty(4));
        table.addCell(totalLabel("Total").setBorderTop(new SolidBorder(1)));
        table.addCell(totalValue("₹ " + bill.getGrandTotal()).setBorderTop(new SolidBorder(1)));
    }

    private Cell summaryLabel(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell summaryValue(String text) {
        return new Cell()
                .add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell totalLabel(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell totalValue(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell empty(int colspan) {
        return new Cell(1, colspan).setBorder(Border.NO_BORDER);
    }

    private Paragraph space(int lines) {
        return new Paragraph("\n".repeat(lines));
    }

}
