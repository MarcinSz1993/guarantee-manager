package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.LogsType;
import org.springframework.stereotype.Component;

@Component
public class PdfLogRendererFactory {

    public static PdfLogRender getPdfLogRender(LogsType logsType){
        if (logsType == null){
            throw new IllegalArgumentException("logsType is null");
        }
        if (logsType.equals(LogsType.RESET_PASSWORD)){
            return new ResetPasswordLogRenderer();
        } else if (logsType.equals(LogsType.REMOVED_GUARANTEE_HISTORY)) {
            return new RemovedGuaranteeHistoryLogRender();
        } else  {
            return new AddedGuaranteeHistoryLogRenderer();
        }
    }
}
