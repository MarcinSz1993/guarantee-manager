package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditResponse;
import com.marcinsz.backend.audit.AuditService;
import com.marcinsz.backend.audit.LogsType;
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

    public byte[] createUserLogsPdfDocument(String userEmail) throws IOException {
        AuditResponse audit = auditService.getAudit(userEmail, 0, 100);
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PDDocument pdfDocument = new PDDocument();
            InputStream fontAsStream = getClass().getClassLoader().getResourceAsStream("fonts/Roboto.ttf")){
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
            float startY = pageHeight - 70;
            float marginX = 50;
            float currentTextPosition = startY;
            for (int i = 0; i < audit.getAuditLogs().size(); i++) {
                if (currentTextPosition < 100) {
                    page = new PDPage();
                    pdfDocument.addPage(page);
                    int count = pdfDocument.getPages().getCount();
                    contentStream.close();
                    contentStream = new PDPageContentStream(pdfDocument, pdfDocument.getPage(count - 1));
                    currentTextPosition = pageHeight - 50;
                }


                if (audit.getAuditLogs().get(i).getLogsType().equals(LogsType.RESET_PASSWORD)) {
                    CommonLogsProperties commonLogsProperties = getCommonLogsProperties(audit, i);
                    String accountCreationDate = "Account has been created on: " + audit.getAuditLogs().get(i).getLogDetails().get("accountCreationDate");
                    showCommonLogsProperties(contentStream, font, counter, marginX, currentTextPosition, commonLogsProperties.logType(), commonLogsProperties.firstName(), commonLogsProperties.lastName(), commonLogsProperties.email(), commonLogsProperties.operationTime());
                    contentStream.newLine();
                    contentStream.showText(accountCreationDate);
                    contentStream.endText();
                    drawLineSeparator(contentStream, marginX, currentTextPosition, pageWidth, 1.0f, 1.0f);
                    counter++;
                    currentTextPosition = currentTextPosition - 90;

                } else if (audit.getAuditLogs().get(i).getLogsType().equals(LogsType.ADDED_GUARANTEE_HISTORY)) {
                    CommonLogsProperties commonLogsProperties = getCommonLogsProperties(audit, i);
                    String notes = "Written note: " + audit.getAuditLogs().get(i).getLogDetails().get("notes");
                    String feedback = "Feedback was positive: " + audit.getAuditLogs().get(i).getLogDetails().get("positiveFeedback");
                    String guaranteeId = "Guarantee identification number: " + audit.getAuditLogs().get(i).getLogDetails().get("guaranteeId");
                    showCommonLogsProperties(contentStream, font, counter, marginX, currentTextPosition, commonLogsProperties.logType(), commonLogsProperties.firstName(), commonLogsProperties.lastName(), commonLogsProperties.email(), commonLogsProperties.operationTime());
                    contentStream.newLine();
                    contentStream.showText(feedback);
                    contentStream.newLine();
                    contentStream.showText(notes);
                    contentStream.newLine();
                    contentStream.showText(guaranteeId);
                    contentStream.endText();
                    drawLineSeparator(contentStream, marginX, currentTextPosition, pageWidth, 0.0f, 1.0f);

                    counter++;
                    currentTextPosition = currentTextPosition - 120;
                } else if (audit.getAuditLogs().get(i).getLogsType().equals(LogsType.REMOVED_GUARANTEE_HISTORY)) {
                    String guaranteeId = "Guarantee identification number: " + audit.getAuditLogs().get(i).getLogDetails().get("guaranteeId");
                    CommonLogsProperties commonLogsProperties = getCommonLogsProperties(audit, i);
                    showCommonLogsProperties(contentStream, font, counter, marginX, currentTextPosition, commonLogsProperties.logType(), commonLogsProperties.firstName(), commonLogsProperties.lastName(), commonLogsProperties.email(), commonLogsProperties.operationTime());
                    contentStream.newLine();
                    contentStream.showText(guaranteeId);
                    contentStream.endText();
                    drawLineSeparator(contentStream, marginX, currentTextPosition, pageWidth, 1.0f, 0.0f);

                    counter++;
                    currentTextPosition = currentTextPosition - 90;
                }

            }
            contentStream.close();
            pdfDocument.save(outputStream);
            return outputStream.toByteArray();

        }
    }

    private CommonLogsProperties getCommonLogsProperties(AuditResponse audit, int i) {
        String logType = audit.getAuditLogs().get(i).getLogsType().toString();
        String firstName = "First name: " + audit.getAuditLogs().get(i).getLogDetails().get("guaranteeOwnerName");
        String lastName = "Last name: " + audit.getAuditLogs().get(i).getLogDetails().get("guaranteeOwnerLastName");
        String email = "Email: " + audit.getAuditLogs().get(i).getLogDetails().get("guaranteeOwnerEmail");
        String operationTime = "Operation time: " + audit.getAuditLogs().get(i).getLogDetails().get("operationTime");
        return new CommonLogsProperties(logType, firstName, lastName, email, operationTime);
    }


    private void drawLineSeparator(PDPageContentStream contentStream, float marginX, float currentTextPosition, float pageWidth, float r, float g) throws IOException {
        contentStream.setStrokingColor(r, g, (float) 0.0);
        contentStream.moveTo(marginX, currentTextPosition - 2);
        contentStream.lineTo(pageWidth - marginX, currentTextPosition - 2);
        contentStream.stroke();
    }


    private void showCommonLogsProperties(PDPageContentStream contentStream, PDType0Font font, int counter, float marginX, float currentTextPosition, String logType, String firstName, String lastName, String email, String operationTime) throws IOException {
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
    }
}
