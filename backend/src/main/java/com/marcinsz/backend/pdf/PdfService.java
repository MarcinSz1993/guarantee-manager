package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditResponse;
import com.marcinsz.backend.audit.AuditService;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class PdfService {
    private final AuditService auditService;
    private final PdfLogRendererFactory pdfLogRendererFactory;
    private final UserRepository userRepository;

    public byte[] createUserLogsPdfDocument(String userEmail) throws IOException {
        userRepository.findByEmail(userEmail).orElseThrow(() -> UserNotFoundException.byEmail(userEmail));
        AuditResponse audit = auditService.getAudit(userEmail, 0, 100);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); PDDocument pdfDocument = new PDDocument(); InputStream fontAsStream = getClass().getClassLoader().getResourceAsStream("fonts/Roboto.ttf")) {
            PDPage page = new PDPage();
            pdfDocument.addPage(page);
            float pageWidth = pdfDocument.getPage(0).getMediaBox().getWidth();
            float pageHeight = pdfDocument.getPage(0).getMediaBox().getHeight();
            PDDocumentInformation documentInfo = new PDDocumentInformation();
            documentInfo.setAuthor("Marcin Szabała");
            documentInfo.setTitle("User Logs");
            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, pdfDocument.getPage(0));

            PDType0Font font = PDType0Font.load(pdfDocument, fontAsStream);
            contentStream.setLeading(14);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            String welcomeText = "Below you find logs of user with email " + userEmail;
            float textWidth = font.getStringWidth(welcomeText) / 1000 * 12;
            contentStream.newLineAtOffset((pageWidth - textWidth) / 2, pageHeight - 30);
            contentStream.showText(welcomeText);
            contentStream.endText();
            contentStream.moveTo(0, pageHeight - 40);
            contentStream.lineTo(pageWidth, pageHeight - 40);
            contentStream.stroke();

            int counter = 1;
            float currentTextPosition = pageHeight - 70;
            for (int i = 0; i < audit.getAuditLogs().size(); i++) {
                if (currentTextPosition < 100) {
                    page = new PDPage();
                    pdfDocument.addPage(page);
                    int count = pdfDocument.getPages().getCount();
                    contentStream.close();
                    contentStream = new PDPageContentStream(pdfDocument, pdfDocument.getPage(count - 1));
                    currentTextPosition = pageHeight - 50;
                }

                try {
                    PdfLogRender renderer = pdfLogRendererFactory.getPdfLogRender(
                            audit.getAuditLogs().get(i).getLogsType());
                    float blockHeight = renderer.render(audit.getAuditLogs().get(i), contentStream, pdfDocument, font, counter,
                            currentTextPosition);
                    currentTextPosition -= blockHeight;
                    counter++;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            contentStream.close();
            pdfDocument.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}



