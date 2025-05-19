package com.marcinsz.backend.audit;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuditResponse {
    private List<AuditLogsModel> auditLogs;
    private int page;
    private int pageSize;
    private int totalPages;
    private int totalElements;
    private boolean firstPage;
    private boolean lastPage;
}
