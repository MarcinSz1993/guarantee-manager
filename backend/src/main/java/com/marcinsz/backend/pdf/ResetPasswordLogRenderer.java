package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ResetPasswordLogRenderer extends BasicLogRenderer implements PdfLogRender{
    @Override
    public float render(AuditLogsModel log, PDPageContentStream contentStream, PDDocument document, PDType0Font font, int counter,float currentTextPosition) throws IOException {
        float pageWidth = getPageWidth(document);

        CommonLogsProperties commonLogsProperties = getCommonLogsProperties(log);
        String accountCreationDate = "Account has been created on: " + log.getLogDetails().get("accountCreationDate");

        showCommonText(contentStream,font,currentTextPosition,counter, commonLogsProperties.logType(), commonLogsProperties.firstName()
                ,commonLogsProperties.lastName(),commonLogsProperties.email(),commonLogsProperties.operationTime());
        contentStream.showText(accountCreationDate);
        contentStream.endText();

        drawSeparationLine(contentStream,currentTextPosition,pageWidth,1.0f, 1.0f);
        return 90f;
    }
}
