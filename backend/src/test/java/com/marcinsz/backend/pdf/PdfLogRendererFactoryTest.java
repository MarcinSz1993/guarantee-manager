package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.LogsType;
import com.marcinsz.backend.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfLogRendererFactoryTest {

    private PdfLogRendererFactory pdfLogRendererFactory;
    private Map<String,PdfLogRender> renderers;

    @Mock
    private ResetPasswordLogRenderer resetPasswordLogRenderer;
    @Mock
    private RemovedGuaranteeHistoryLogRenderer removedGuaranteeHistoryLogRenderer;
    @Mock
    private AddedGuaranteeHistoryLogRenderer addedGuaranteeHistoryLogRenderer;


    @BeforeEach
    void setUp() {
        renderers = new HashMap<>();
        renderers.put("resetPasswordLogRenderer",resetPasswordLogRenderer);
        renderers.put("removedGuaranteeHistoryLogRenderer",removedGuaranteeHistoryLogRenderer);
        renderers.put("addedGuaranteeHistoryLogRenderer",addedGuaranteeHistoryLogRenderer);
        pdfLogRendererFactory = new PdfLogRendererFactory(renderers);
    }

    @Test
    void getPdfLogRenderShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenBeanOfPdfLogRenderClassDoesNotExist(){
        renderers.remove("resetPasswordLogRenderer");
        renderers.put("fakeBeanLogRenderer",resetPasswordLogRenderer);
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfLogRendererFactory.getPdfLogRender(LogsType.RESET_PASSWORD));
        assertEquals("No renderer found for bean name " + "resetPasswordLogRenderer", invalidInputException.getMessage());
    }

    @Test
    void getPdfLogRenderShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenLogsTypeIsNotHandled(){
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfLogRendererFactory.getPdfLogRender(LogsType.UNKNOWN_TYPE_FOR_TESTS));
        assertEquals("logsType is not recognized " + LogsType.UNKNOWN_TYPE_FOR_TESTS, invalidInputException.getMessage());
    }


    @Test
    void getPdfLogRenderShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenLogsTypeIsNull(){
        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfLogRendererFactory.getPdfLogRender(null));
        assertEquals("logsType is null", invalidInputException.getMessage());
    }

    @Test
    void getPdfLogRenderShouldSuccessfullyReturnRendererAccordingToTheBeanNameADDED_GUARANTEE_HISTORY() {
        LogsType logsType = LogsType.ADDED_GUARANTEE_HISTORY;
        PdfLogRender render = pdfLogRendererFactory.getPdfLogRender(logsType);
        assertNotNull(render);
        assertEquals(renderers.get("addedGuaranteeHistoryLogRenderer"),render);
    }

    @Test
    void getPdfLogRenderShouldSuccessfullyReturnRendererAccordingToTheBeanNameREMOVED_GUARANTEE_HISTORY() {
        LogsType logsType = LogsType.REMOVED_GUARANTEE_HISTORY;
        PdfLogRender render = pdfLogRendererFactory.getPdfLogRender(logsType);
        assertNotNull(render);
        assertEquals(renderers.get("removedGuaranteeHistoryLogRenderer"),render);
    }

    @Test
    void getPdfLogRenderShouldSuccessfullyReturnRendererAccordingToTheBeanNameRESET_PASSWORD() {
        LogsType logsType = LogsType.RESET_PASSWORD;
        PdfLogRender render = pdfLogRendererFactory.getPdfLogRender(logsType);
        assertNotNull(render);
        assertEquals(renderers.get("resetPasswordLogRenderer"),render);
    }

}