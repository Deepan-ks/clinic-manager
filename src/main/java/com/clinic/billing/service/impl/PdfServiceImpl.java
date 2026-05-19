package com.clinic.billing.service.impl;

import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.BillItem;
import com.clinic.billing.service.PdfService;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

import static com.clinic.billing.utils.Constants.*;

@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public ByteArrayOutputStream buildPdf(Bill bill) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = buildDocumentLayout(pdf);

        buildInfoLayout(bill, document);

        buildTableLayout(bill, document);

        buildSignatureLayout(document);

        document.close();

        return outputStream;
    }

    private void buildTableLayout(Bill bill, Document document) {
        Table table = buildMainTable(bill);
        // SUMMARY INSIDE TABLE
        addSummary(table, bill);
        document.add(table);
    }

    private void buildSignatureLayout(Document document) {
        document.add(space(3));

        LineSeparator line = new LineSeparator(new SolidLine());
        line.setWidth(UnitValue.createPercentValue(30));
        line.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        document.add(line);

        document.add(new Paragraph(SIGNATURE).setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
    }

    private Table buildMainTable(Bill bill) {
        float[] cols = {1, 3, 5, 2, 2, 2};
        Table table = new Table(UnitValue.createPercentArray(cols)).useAllAvailableWidth();

        addHeaders(table);

        int i = 1;
        for (BillItem item : bill.getItems()) {
            table.addCell(bodyCell(String.valueOf(i++)));
            table.addCell(bodyCell(item.getServiceName()));
            table.addCell(bodyCell(item.getService().getDescription() != null ? item.getService().getDescription() : "-"));
            table.addCell(rightCell("₹ " + item.getUnitPrice()));
            table.addCell(rightCell(String.valueOf(item.getQuantity())));
            table.addCell(rightCell("₹ " + item.getLineTotal()));
        }
        return table;
    }

    private void buildInfoLayout(Bill bill, Document document) {
        Table info = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();

        info.addCell(patientInfoCell(BILL_TO,
                PATIENT_NAME + bill.getPatient().getName(),
                AGE_GENDER + bill.getPatient().getAge() + " / " + bill.getPatient().getGender(),
                MOBILE + bill.getPatient().getPhone(),
                ADDRESS + bill.getPatient().getAddress()));

        info.addCell(infoCell(INVOICE,
                INVOICE_NO + bill.getBillNumber(),
                DATE + bill.getCreatedTime().toLocalDate()));

        document.add(info);
        document.add(space(1));
    }

    private @NonNull Document buildDocumentLayout(PdfDocument pdf) {
        Document document = new Document(pdf);

        // =========================
        // CLINIC HEADER (CLEAN STYLE)
        // =========================

        document.add(space(1));

        document.add(new Paragraph(MEDISMILE_HEALTH_CARE).setBold().setFontSize(22).setTextAlignment(TextAlignment.LEFT));

        // underline
        LineSeparator headerLine = new LineSeparator(new SolidLine());
        headerLine.setWidth(UnitValue.createPercentValue(80));
        headerLine.setHorizontalAlignment(HorizontalAlignment.LEFT);
        document.add(headerLine);

        // address + email + mobile
        document.add(new Paragraph(FULL_ADDRESS + EMAIL_PHONE).setFontSize(12));

        document.add(space(2));
        return document;
    }

    private Cell infoCell(String title, String line1, String line2) {
        return new Cell().add(new Paragraph(title).setBold().setFontSize(11))
                .add(new Paragraph(line1).setFontSize(10))
                .add(new Paragraph(line2).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }

    private Cell patientInfoCell(String title, String line1, String line2, String line3, String line4) {
        return new Cell().add(new Paragraph(title).setBold().setFontSize(11))
                .add(new Paragraph(line1).setFontSize(10))
                .add(new Paragraph(line2).setFontSize(10))
                .add(new Paragraph(line3).setFontSize(10))
                .add(new Paragraph(line4).setFontSize(10))
                .setBorder(Border.NO_BORDER);
    }

    private void addHeaders(Table table) {
        String[] headers = {SL_NO, SERVICE, DESCRIPTION, PRICE, QUANTITY, TOTAL};

        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setFontSize(10))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5));
        }
    }

    private Cell bodyCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
                .setPadding(4)
                .setBorderBottom(new SolidBorder(0.5f));
    }

    private Cell rightCell(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(4)
                .setBorderBottom(new SolidBorder(0.5f));
    }

    private void addSummary(Table table, Bill bill) {

        table.addCell(empty(4));
        table.addCell(summaryLabel(SUB_TOTAL));
        table.addCell(summaryValue("₹ " + bill.getSubtotal()));

        table.addCell(empty(4));
        table.addCell(summaryLabel(DISCOUNT_IN_SYMBOL));
        table.addCell(summaryValue("₹ " + bill.getDiscountAmount()));

        table.addCell(empty(4));
        table.addCell(summaryLabel(DISCOUNT_IN_PERCENT));
        table.addCell(summaryValue("% " + bill.getDiscountPercent()));

        table.addCell(empty(4));
        table.addCell(totalLabel(TOTAL).setBorderTop(new SolidBorder(1)));
        table.addCell(totalValue("₹ " + bill.getGrandTotal()).setBorderTop(new SolidBorder(1)));
    }

    private Cell summaryLabel(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell summaryValue(String text) {
        return new Cell().add(new Paragraph(text).setFontSize(10))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell totalLabel(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(11))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER);
    }

    private Cell totalValue(String text) {
        return new Cell().add(new Paragraph(text).setBold().setFontSize(11))
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
