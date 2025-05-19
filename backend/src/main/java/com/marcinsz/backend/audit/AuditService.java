package com.marcinsz.backend.audit;

import com.marcinsz.backend.exception.UserNotFoundException;
import com.marcinsz.backend.mongodb.*;
import com.marcinsz.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository;
    private final RemovedGuaranteeHistoryMongoRepository removedGuaranteeHistoryMongoRepository;
    private final ResetPasswordMongoRepository resetPasswordMongoRepository;
    private final UserRepository userRepository;


    public AuditResponse getAudit(String email, int page, int size) {
        List<AuditLogsModel> result = new ArrayList<>();

        userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.byEmail(email));

        guaranteeHistoryMongoRepository.findAllByGuaranteeOwnerEmail(email)
                .forEach(guaranteeHistoryDocument -> result.add(
                        getAddedGuaranteeHistoryLogs(guaranteeHistoryDocument)));

        removedGuaranteeHistoryMongoRepository.findAllByGuaranteeOwnerEmail(email)
                .forEach(removedGuaranteeHistoryDocument -> result.add(
                        getRemovedGuaranteeHistoryLogs(removedGuaranteeHistoryDocument)));

        resetPasswordMongoRepository.findAllByEmail(email)
                .forEach(resetPasswordDocument -> result.add(
                        getResetPasswordLogs(resetPasswordDocument)));

        int fromIndex = page * size;
        int totalElements = result.size();
        int totalPages = (int) Math.ceil((double) totalElements/size);

        if (fromIndex >= totalElements) {
            return protectionAgainstBoundException(page, size, totalPages, totalElements);
        }
        List<AuditLogsModel> paginatedLogsList = result.stream()
                .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
                .skip(fromIndex)
                .limit(size)
                .toList();


        return getAuditResponse(page, size, paginatedLogsList, totalPages, totalElements);
    }

    private static AuditResponse protectionAgainstBoundException(int page, int size, int totalPages, int totalElements) {
        return AuditResponse.builder()
                .auditLogs(List.of())
                .page(page)
                .pageSize(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .firstPage(page == 0)
                .lastPage(true)
                .build();
    }

    private static AuditResponse getAuditResponse(int page, int size, List<AuditLogsModel> paginatedLogsList, int totalPages, int totalElements) {
        return AuditResponse.builder()
                .auditLogs(paginatedLogsList)
                .page(page)
                .pageSize(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .firstPage(page == 0)
                .lastPage(page == totalPages - 1)
                .build();
    }

    private static ResetPasswordLogs getResetPasswordLogs(ResetPasswordDocument resetPasswordDocument) {
        return ResetPasswordLogs.builder()
                .logsType(LogsType.RESET_PASSWORD)
                .timestamp(resetPasswordDocument.getOperationTime())
                .logDetails(Map.of(
                        "firstName", resetPasswordDocument.getFirstName(),
                        "lastName", resetPasswordDocument.getLastName(),
                        "email", resetPasswordDocument.getEmail(),
                        "operationTime", resetPasswordDocument.getOperationTime().toString(),
                        "accountCreationDate", resetPasswordDocument.getAccountCreationDate().toString()
                ))
                .build();
    }

    private static RemovedGuaranteeHistoryLogs getRemovedGuaranteeHistoryLogs(RemovedGuaranteeHistoryDocument removedGuaranteeHistoryDocument) {
        return RemovedGuaranteeHistoryLogs.builder()
                .logsType(LogsType.REMOVED_GUARANTEE_HISTORY)
                .timestamp(removedGuaranteeHistoryDocument.getOperationTime())
                .logDetails(Map.of(
                        "guaranteeId", removedGuaranteeHistoryDocument.getGuaranteeId().toString(),
                        "guaranteeOwnerName", removedGuaranteeHistoryDocument.getGuaranteeOwnerName(),
                        "guaranteeOwnerLastName", removedGuaranteeHistoryDocument.getGuaranteeOwnerLastName(),
                        "guaranteeOwnerEmail", removedGuaranteeHistoryDocument.getGuaranteeOwnerEmail(),
                        "operationTime", removedGuaranteeHistoryDocument.getOperationTime().toString()
                ))
                .build();
    }

    private static AddedGuaranteeHistoryLogs getAddedGuaranteeHistoryLogs(GuaranteeHistoryDocument guaranteeHistoryDocument) {
        return AddedGuaranteeHistoryLogs.builder()
                .logsType(LogsType.ADDED_GUARANTEE_HISTORY)
                .timestamp(guaranteeHistoryDocument.getOperationTime())
                .logDetails(Map.of(
                        "guaranteeId", guaranteeHistoryDocument.getGuaranteeId().toString(),
                        "guaranteeOwnerName", guaranteeHistoryDocument.getGuaranteeOwnerName(),
                        "guaranteeOwnerLastName", guaranteeHistoryDocument.getGuaranteeOwnerLastName(),
                        "guaranteeOwnerEmail", guaranteeHistoryDocument.getGuaranteeOwnerEmail(),
                        "notes", guaranteeHistoryDocument.getNotes(),
                        "operationTime", guaranteeHistoryDocument.getOperationTime().toString(),
                        "positiveFeedback", String.valueOf(guaranteeHistoryDocument.isPositiveFeedback())
                ))
                .build();
    }
}