package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import com.marcinsz.backend.exception.InvalidInputException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AddedGuaranteeHistoryLogRenderer extends BasicLogRenderer implements PdfLogRender {
    @Override
    public float render(AuditLogsModel log, PDPageContentStream contentStream, PDDocument document, PDType0Font font, int counter, float currentTextPosition) throws IOException {
        float pageWidth = getPageWidth(document);

        CommonLogsProperties commonLogsProperties = getCommonLogsProperties(log);

        if (log.getLogDetails().get("notes").length() > 55){
            throw new InvalidInputException("Written note should have not more than 55 characters");
        }

        String notes = "Written note: " + log.getLogDetails().get("notes");
        String feedback = "Feedback was positive: " + log.getLogDetails().get("positiveFeedback");
        String guaranteeId = "Guarantee identification number: " + log.getLogDetails().get("guaranteeId");

        showCommonText(contentStream,font,currentTextPosition,counter, commonLogsProperties.logType(), commonLogsProperties.firstName()
                ,commonLogsProperties.lastName(),commonLogsProperties.email(),commonLogsProperties.operationTime());
        contentStream.showText(feedback);
        contentStream.newLine();
        contentStream.showText(notes);
        contentStream.newLine();
        contentStream.showText(guaranteeId);
        contentStream.endText();

        drawSeparationLine(contentStream,currentTextPosition,pageWidth,0.0f, 1.0f);

        return 120f;
    }
}
