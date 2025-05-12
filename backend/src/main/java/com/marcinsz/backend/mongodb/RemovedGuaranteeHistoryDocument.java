package com.marcinsz.backend.mongodb;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class RemovedGuaranteeHistoryDocument {

    private String id;
    private Long guaranteeId;
    private String guaranteeOwnerName;
    private String guaranteeOwnerLastName;
    private String guaranteeOwnerEmail;
    private LocalDateTime operationTime;
}
