package com.LocaDj.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.LocaDj.models.User;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    public byte[] gerarRelatorioUsuarios(List<User> usuarios) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 12);

        Paragraph title = new Paragraph("Relatório de Usuários - Loca DJ", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 3, 4});

        table.addCell(new PdfPCell(new Phrase("ID", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Nome", headerFont)));
        table.addCell(new PdfPCell(new Phrase("Email", headerFont)));

        for (User user : usuarios) {
            table.addCell(new PdfPCell(new Phrase(user.getId().toString(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(user.getName(), cellFont)));
            table.addCell(new PdfPCell(new Phrase(user.getEmail(), cellFont)));
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }
}