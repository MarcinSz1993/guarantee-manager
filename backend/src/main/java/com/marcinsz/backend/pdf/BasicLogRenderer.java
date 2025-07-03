package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;

@Getter
public abstract class BasicLogRenderer {
    protected final static float MARGIN_X = 50;
    protected final static int FONT_SIZE = 12;
    protected final static float LEADING = 14.5f;

    protected float getPageWidth(PDDocument document){
        return document.getPage(0).getMediaBox().getWidth();
    }

    protected String getLogType(AuditLogsModel auditLogsModel){
        return auditLogsModel.getLogsType().toString();
    }

    protected String getFirstName(AuditLogsModel auditLogsModel){
        return "First name: " + auditLogsModel.getLogDetails().get("guaranteeOwnerName");
    }

    protected String getLastName(AuditLogsModel auditLogsModel){
        return "Last name: " + auditLogsModel.getLogDetails().get("guaranteeOwnerLastName");
    }

    protected String getEmail(AuditLogsModel auditLogsModel){
        return "Email: " + auditLogsModel.getLogDetails().get("guaranteeOwnerEmail");
    }

    protected String getOperationTime(AuditLogsModel auditLogsModel){
        return "Operation time: " + auditLogsModel.getLogDetails().get("operationTime");
    }

    protected void drawSeparationLine(PDPageContentStream contentStream,float currentTextPosition, float pageWidth, float r, float g) throws IOException {
        contentStream.setStrokingColor(r,g,0.0f);
        contentStream.moveTo(MARGIN_X, currentTextPosition - 2);
        contentStream.lineTo(pageWidth - MARGIN_X, currentTextPosition - 2);
        contentStream.stroke();
    }

    protected void showCommonText(PDPageContentStream contentStream, PDType0Font font,float currentTextPosition,
                                  int counter, String logType, String firstName, String lastName, String email,String operationTime) throws IOException {

        contentStream.beginText();
        contentStream.setLeading(LEADING);
        contentStream.setFont(font, FONT_SIZE);
        contentStream.newLineAtOffset(MARGIN_X, currentTextPosition);
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
    }

    protected CommonLogsProperties getCommonLogsProperties(AuditLogsModel log){
        return new CommonLogsProperties(getLogType(log),getFirstName(log),getLastName(log),getEmail(log),getOperationTime(log));
    }
}
