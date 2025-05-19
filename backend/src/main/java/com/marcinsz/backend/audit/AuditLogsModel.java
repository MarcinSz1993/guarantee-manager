package com.marcinsz.backend.audit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@SuperBuilder
@AllArgsConstructor
public abstract class AuditLogsModel {
    private LogsType logsType;
    private LocalDateTime timestamp;
    private Map<String,String> logDetails;

}
