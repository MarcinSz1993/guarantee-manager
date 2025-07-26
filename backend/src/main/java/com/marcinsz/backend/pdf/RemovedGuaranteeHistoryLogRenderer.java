package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.AuditLogsModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RemovedGuaranteeHistoryLogRenderer extends BasicLogRenderer implements PdfLogRender {
    @Override
    public float render(AuditLogsModel log, PDPageContentStream contentStream, PDDocument document, PDType0Font font, int counter, float currentTextPosition) throws IOException {
        float pageWidth = getPageWidth(document);

        CommonLogsProperties commonLogsProperties = getCommonLogsProperties(log);
        String guaranteeId = "Guarantee identification number: " + log.getLogDetails().get("guaranteeId");


        showCommonText(contentStream,font,currentTextPosition,counter, commonLogsProperties.logType(), commonLogsProperties.firstName()
                ,commonLogsProperties.lastName(),commonLogsProperties.email(),commonLogsProperties.operationTime());
        contentStream.showText(guaranteeId);
        contentStream.endText();

        drawSeparationLine(contentStream,currentTextPosition,pageWidth,1.0f, 0.0f);

        return 90f;
    }
}
