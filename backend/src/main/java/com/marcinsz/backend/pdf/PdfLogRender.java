package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public interface PdfLogRender {
    float render(AuditLogsModel log, PDPageContentStream contentStream, PDDocument document, PDType0Font font, int counter, float currentTextPosition) throws IOException;
}
