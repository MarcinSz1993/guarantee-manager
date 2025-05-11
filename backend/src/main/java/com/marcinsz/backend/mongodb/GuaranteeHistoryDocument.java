package com.marcinsz.backend.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class GuaranteeHistoryDocument {
    @Id
    private String id;
    private Long guaranteeId;
    private String guaranteeOwnerName;
    private String guaranteeOwnerLastName;
    private String guaranteeOwnerEmail;
    private String notes;
    private LocalDateTime operationTime;
    private boolean positiveFeedback;
}
