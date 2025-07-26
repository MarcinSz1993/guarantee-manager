package com.marcinsz.backend.pdf;

import com.marcinsz.backend.audit.*;
import com.marcinsz.backend.exception.InvalidInputException;
import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.notification.NotificationPreference;
import com.marcinsz.backend.user.Role;
import com.marcinsz.backend.user.User;
import com.marcinsz.backend.user.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    private User user;
    private String userEmail;

    @Mock
    private AuditService auditService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PdfLogRendererFactory pdfLogRendererFactory;
    @InjectMocks
    private PdfService pdfService;

    @BeforeEach
    void setUp() {
        user = createUser();
        userEmail = user.getEmail();
    }

    @Test
    void createUserLogsPdfDocumentShouldNotCreateNewPageWhenSpaceIsEnough() throws IOException {

        AddedGuaranteeHistoryLogs addedGuaranteeHistoryLogs = createAddedGuaranteeHistoryLogs();
        AuditResponse auditResponse = AuditResponse.builder()
                .auditLogs(List.of(addedGuaranteeHistoryLogs))
                .page(0)
                .pageSize(1)
                .totalPages(1)
                .totalElements(1)
                .firstPage(true)
                .lastPage(true)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(auditResponse);

        PdfLogRender renderer = mock(PdfLogRender.class);
        when(renderer.render(any(), any(), any(), any(), anyInt(), anyFloat())).thenReturn(20f);
        when(pdfLogRendererFactory.getPdfLogRender(LogsType.ADDED_GUARANTEE_HISTORY)).thenReturn(renderer);

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(userEmail);
        assertNotNull(userLogsPdfDocument);

        try (PDDocument document = Loader.loadPDF(userLogsPdfDocument)) {
            int numberOfPages = document.getPages().getCount();

            assertEquals(1, numberOfPages);
            assertTrue(numberOfPages < 2);
        }

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, times(1)).getPdfLogRender(any());
    }


    @Test
    void createUserLogsPdfDocumentShouldCreateNewPageWhenSpaceIsTooSmall() throws IOException {

        List<AuditLogsModel> auditLogs = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ResetPasswordLogs log = createResetPasswordLog();
            log.setLogsType(LogsType.RESET_PASSWORD);
            auditLogs.add(log);
        }

        AuditResponse auditResponse = AuditResponse.builder()
                .auditLogs(auditLogs)
                .page(0)
                .pageSize(100)
                .totalPages(1)
                .totalElements(20)
                .firstPage(true)
                .lastPage(true)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(auditResponse);

        PdfLogRender renderer = mock(PdfLogRender.class);
        when(renderer.render(any(), any(), any(), any(), anyInt(), anyFloat())).thenReturn(600f);

        when(pdfLogRendererFactory.getPdfLogRender(LogsType.RESET_PASSWORD)).thenReturn(renderer);

        byte[] pdfBytes = pdfService.createUserLogsPdfDocument(userEmail);

        assertNotNull(pdfBytes);

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            assertTrue(document.getNumberOfPages() > 1);
        }

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, times(20)).getPdfLogRender(any());
    }


    @Test
    void createUserLogsPdfDocumentShouldThrowExceptionWithSpecifiedCommunicationWhenLogTypeIsIncorrect() {

        ResetPasswordLogs resetPasswordLogWithNotExistingLogType = createResetPasswordLog();
        resetPasswordLogWithNotExistingLogType.setLogsType(LogsType.UNKNOWN_TYPE_FOR_TESTS);
        AuditResponse auditResponse = AuditResponse.builder()
                .auditLogs(List.of(resetPasswordLogWithNotExistingLogType))
                .page(0)
                .pageSize(1)
                .totalPages(1)
                .totalElements(1)
                .firstPage(true)
                .lastPage(true)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(auditResponse);
        when(pdfLogRendererFactory.getPdfLogRender(LogsType.UNKNOWN_TYPE_FOR_TESTS)).thenThrow(new IllegalArgumentException("logsType is not recognized " + resetPasswordLogWithNotExistingLogType.getLogsType().toString()));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> pdfService.createUserLogsPdfDocument(userEmail));
        assertEquals("logsType is not recognized " + resetPasswordLogWithNotExistingLogType.getLogsType().toString(), illegalArgumentException.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, times(1)).getPdfLogRender(LogsType.UNKNOWN_TYPE_FOR_TESTS);
    }

    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenAuditLogsIsNull() {
        AuditResponse nullAuditLogs = AuditResponse.builder()
                .auditLogs(null)
                .page(0)
                .pageSize(0)
                .totalPages(0)
                .totalElements(0)
                .firstPage(true)
                .lastPage(true)
                .build();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(nullAuditLogs);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(userEmail));

        assertEquals("No audit logs found", invalidInputException.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, never()).getPdfLogRender(any(LogsType.class));
    }


    @Test
    void createUserLogsPdfDocumentShouldThrowInvalidInputExceptionWithSpecifiedCommunicationWhenUserHasNoLogs() {
        AuditResponse emptyAuditLogs = AuditResponse.builder()
                .auditLogs(Collections.emptyList())
                .page(0)
                .pageSize(0)
                .totalPages(0)
                .totalElements(0)
                .firstPage(true)
                .lastPage(true)
                .build();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(emptyAuditLogs);

        InvalidInputException invalidInputException = assertThrows(InvalidInputException.class, () -> pdfService.createUserLogsPdfDocument(userEmail));

        assertEquals("No audit logs found", invalidInputException.getMessage());

        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, never()).getPdfLogRender(any(LogsType.class));
    }


    @Test
    void createUserLogsPdfDocumentShouldSuccessfullyCreatePdfDocument() throws IOException {
        ResetPasswordLogs resetPasswordLog = createResetPasswordLog();
        AddedGuaranteeHistoryLogs addedGuaranteeHistoryLogs = createAddedGuaranteeHistoryLogs();
        RemovedGuaranteeHistoryLogs removedGuaranteeHistoryLogs = createRemovedGuaranteeHistoryLogs();
        AuditResponse auditResponse = createAuditResponse(resetPasswordLog, addedGuaranteeHistoryLogs, removedGuaranteeHistoryLogs);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(auditService.getAudit(userEmail, 0, 100)).thenReturn(auditResponse);

        PdfLogRender resetPasswordRenderer = mock(PdfLogRender.class);
        PdfLogRender addedGuaranteeHistoryRenderer = mock(PdfLogRender.class);
        PdfLogRender removedGuaranteeHistoryRenderer = mock(PdfLogRender.class);

        when(resetPasswordRenderer.render(any(), any(), any(), any(), anyInt(), anyFloat())).thenReturn(20f);
        when(addedGuaranteeHistoryRenderer.render(any(), any(), any(), any(), anyInt(), anyFloat())).thenReturn(20f);
        when(removedGuaranteeHistoryRenderer.render(any(), any(), any(), any(), anyInt(), anyFloat())).thenReturn(20f);

        when(pdfLogRendererFactory.getPdfLogRender(resetPasswordLog.getLogsType())).thenReturn(resetPasswordRenderer);
        when(pdfLogRendererFactory.getPdfLogRender(addedGuaranteeHistoryLogs.getLogsType())).thenReturn(addedGuaranteeHistoryRenderer);
        when(pdfLogRendererFactory.getPdfLogRender(removedGuaranteeHistoryLogs.getLogsType())).thenReturn(removedGuaranteeHistoryRenderer);


        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(userEmail);

        assertTrue(userLogsPdfDocument.length > 0);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(auditService, times(1)).getAudit(userEmail, 0, 100);
        verify(pdfLogRendererFactory, times(1)).getPdfLogRender(LogsType.RESET_PASSWORD);
        verify(pdfLogRendererFactory, times(1)).getPdfLogRender(LogsType.ADDED_GUARANTEE_HISTORY);
        verify(pdfLogRendererFactory, times(1)).getPdfLogRender(LogsType.REMOVED_GUARANTEE_HISTORY);

    }

    @Test
    void createUserLogsPdfDocumentShouldThrowUserNotFoundExceptionWithSpecifiedCommunicationWhenUserIsNotFound() {
        String notExistingUserEmail = "not-existing-user@email.com";

        when(userRepository.findByEmail(notExistingUserEmail)).thenReturn(Optional.empty());
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> pdfService.createUserLogsPdfDocument(notExistingUserEmail));

        assertEquals("User with email " + notExistingUserEmail + " not found", userNotFoundException.getMessage());
        verify(userRepository, times(1)).findByEmail(notExistingUserEmail);
        verify(auditService, never()).getAudit(notExistingUserEmail, 0, 100);
        verify(pdfLogRendererFactory, never()).getPdfLogRender(any());

    }


    private AuditResponse createAuditResponse(ResetPasswordLogs resetPasswordLogs, AddedGuaranteeHistoryLogs addedGuaranteeHistoryLogs, RemovedGuaranteeHistoryLogs removedGuaranteeHistoryLogs) {

        return AuditResponse.builder()
                .auditLogs(List.of(resetPasswordLogs, addedGuaranteeHistoryLogs, removedGuaranteeHistoryLogs))
                .page(0)
                .pageSize(3)
                .totalPages(1)
                .totalElements(3)
                .lastPage(false)
                .firstPage(true)
                .build();

    }

    private ResetPasswordLogs createResetPasswordLog() {
        return ResetPasswordLogs.builder()
                .logsType(LogsType.RESET_PASSWORD)
                .timestamp(LocalDateTime.of(2025, 7, 7, 10, 10))
                .logDetails(Map.of(
                        "guaranteeOwnerName", "John",
                        "guaranteeOwnerLastName", "Doe",
                        "guaranteeOwnerEmail", "john@doe.pl",
                        "operationTime", LocalDateTime.of(2025, 7, 7, 10, 10).toString(),
                        "accountCreationDate", LocalDate.of(2025, 1, 1).toString()
                ))
                .build();
    }

    private AddedGuaranteeHistoryLogs createAddedGuaranteeHistoryLogs() {
        return AddedGuaranteeHistoryLogs.builder()
                .logsType(LogsType.ADDED_GUARANTEE_HISTORY)
                .timestamp(LocalDateTime.of(2025, 7, 7, 11, 10))
                .logDetails(Map.of(
                        "guaranteeId", "1",
                        "guaranteeOwnerName", "John",
                        "guaranteeOwnerLastName", "Doe",
                        "guaranteeOwnerEmail", "john@doe.pl",
                        "notes", "Test",
                        "operationTime", LocalDateTime.of(2025, 7, 7, 11, 10).toString(),
                        "positiveFeedback", "true"
                ))
                .build();
    }

    private RemovedGuaranteeHistoryLogs createRemovedGuaranteeHistoryLogs() {
        return RemovedGuaranteeHistoryLogs.builder()
                .logsType(LogsType.REMOVED_GUARANTEE_HISTORY)
                .timestamp(LocalDateTime.of(2025, 7, 7, 12, 10))
                .logDetails(Map.of(
                                "guaranteeId", "2",
                                "guaranteeOwnerName", "John",
                                "guaranteeOwnerLastName", "Doe",
                                "guaranteeOwnerEmail", "john@doe.pl",
                                "operationTime", LocalDateTime.of(2025, 7, 7, 12, 10).toString()
                        )
                )
                .build();
    }

    private User createUser() {
        return User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johnny")
                .password("hashedQwerty")
                .email("john@doe.pl")
                .role(Role.USER)
                .notificationPreference(NotificationPreference.ALL)
                .createdDate(LocalDateTime.of(2025, 1, 1, 6, 0))
                .userEnabled(false)
                .build();
    }
}