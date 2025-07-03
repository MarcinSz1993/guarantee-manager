package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;

public class RemovedGuaranteeHistoryLogRender implements PdfLogRender {
    @Override
    public float render(AuditLogsModel log, PDPageContentStream contentStream, PDDocument document, PDType0Font font, int counter, float currentTextPosition) throws IOException {
        float marginX = 50;
        float pageWidth = document.getPage(0).getMediaBox().getWidth();

        String logType = log.getLogsType().toString();
        String firstName = "First name: " + log.getLogDetails().get("guaranteeOwnerName");
        String lastName = "Last name: " + log.getLogDetails().get("guaranteeOwnerLastName");
        String email = "Email: " + log.getLogDetails().get("guaranteeOwnerEmail");
        String operationTime = "Operation time: " + log.getLogDetails().get("operationTime");
        String guaranteeId = "Guarantee identification number: " + log.getLogDetails().get("guaranteeId");

        contentStream.beginText();
        contentStream.setLeading(14.5f);
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(marginX, currentTextPosition);
        contentStream.showText(counter + ". " + logType);
        contentStream.newLine();
        contentStream.showText(firstName);
        contentStream.newLine();
        contentStream.showText(lastName);
        contentStream.newLine();
        contentStream.showText(email);
        contentStream.newLine();
        contentStream.showText(operationTime);
        contentStream.newLine();
        contentStream.showText(guaranteeId);
        contentStream.endText();

        contentStream.setStrokingColor(1.0f, 0.0f, 0.0f);
        contentStream.moveTo(marginX, currentTextPosition - 2);
        contentStream.lineTo(pageWidth - marginX, currentTextPosition - 2);
        contentStream.stroke();

        return 90f;
    }
}
