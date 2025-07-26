package com.marcinsz.backend.pdf;

public record CommonLogsProperties(
        String logType, String firstName, String lastName, String email,
        String operationTime
) {
}
