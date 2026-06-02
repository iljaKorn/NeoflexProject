package com.neoproject.dossier.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.neoproject.dossier.model.dto.DocumentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentGenerationService {

    private final DealClientService dealClient;

    public File generateDocument(UUID statementId) throws IOException {
        DocumentDto documentDto = dealClient.getDocumentData(statementId);

        File file = File.createTempFile("contract_" + statementId, ".pdf");
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(file));

        document.open();

        BaseFont baseFont = loadBaseFont();

        Font titleFont = new Font(baseFont, 16, Font.BOLD);
        Font normalFont = new Font(baseFont, 12, Font.NORMAL);
        Font boldFont = new Font(baseFont, 12, Font.BOLD);

        Paragraph title = new Paragraph("КРЕДИТНЫЙ ДОГОВОР № " + statementId, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("г. Москва", normalFont));
        document.add(new Paragraph("Дата: " + LocalDate.now(), normalFont));

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("1. СТОРОНЫ ДОГОВОРА", boldFont));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("1.1. Банк \"Имитационный Банк\" в лице менеджера, действующего на основании Устава, " +
                "с одной стороны, и", normalFont));

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("1.2. Клиент: " + documentDto.getLastName() + " " +
                documentDto.getFirstName() + " " +
                ", паспорт серии " + documentDto.getPassportSeries() +
                " № " + documentDto.getPassportNumber()));

        document.add(new Paragraph("\n"));

        // Предмет договора
        document.add(new Paragraph("2. ПРЕДМЕТ ДОГОВОРА", boldFont));
        document.add(new Paragraph("\n"));

        document.add(new Paragraph("2.1. Банк предоставляет Клиенту кредит в размере: " +
                String.format("%.2f", documentDto.getAmount()) + " руб.", normalFont));
        document.add(new Paragraph("2.2. Срок кредита: " + documentDto.getTerm() + " мес.", normalFont));
        document.add(new Paragraph("2.3. Процентная ставка: " + documentDto.getRate() + "% годовых", normalFont));
        document.add(new Paragraph("2.4. Ежемесячный платеж: " +
                String.format("%.2f", documentDto.getMonthlyPayment()) + " руб.", normalFont));

        document.add(new Paragraph("\n"));

        document.add(new Paragraph("3. РЕКВИЗИТЫ СТОРОН", boldFont));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.addCell("Банк:");
        table.addCell("Клиент:");
        table.addCell("ООО \"Имитационный Банк\"");
        table.addCell(documentDto.getLastName() + " " + documentDto.getFirstName());
        table.addCell("ИНН: 7701234567");
        table.addCell("Паспорт: " + documentDto.getPassportSeries() + " " +
                documentDto.getPassportNumber());

        document.add(table);

        document.close();
        return file;
    }

    private BaseFont loadBaseFont() throws IOException, DocumentException {
        ClassPathResource fontResource = new ClassPathResource("fonts/DejaVuSans.ttf");

        try (InputStream fontStream = fontResource.getInputStream()) {
            return BaseFont.createFont(
                    "fonts/DejaVuSans.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    true,
                    fontStream.readAllBytes(),
                    null,
                    false
            );
        }
    }
}
