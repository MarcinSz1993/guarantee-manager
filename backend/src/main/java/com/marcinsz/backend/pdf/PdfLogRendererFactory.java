package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.LogsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PdfLogRendererFactory {

    private final Map<String, PdfLogRender> renderers;

    public PdfLogRender getPdfLogRender(LogsType logsType) {
        if (logsType == null) {
            throw new IllegalArgumentException("logsType is null");
        }
        String beanName;
        if (logsType.equals(LogsType.RESET_PASSWORD)) {
            beanName = "resetPasswordLogRenderer";
        } else if (logsType.equals(LogsType.REMOVED_GUARANTEE_HISTORY)) {
            beanName = "removedGuaranteeHistoryLogRenderer";
        } else if (logsType.equals(LogsType.ADDED_GUARANTEE_HISTORY)) {
            beanName = "addedGuaranteeHistoryLogRenderer";
        } else {
            throw new IllegalArgumentException("logsType is not recognized " + logsType);
        }

        PdfLogRender renderer = renderers.get(beanName);
        if (renderer == null) {
            throw new IllegalStateException("No renderer found for bean name " + beanName);
        }
        return renderer;
    }

}


