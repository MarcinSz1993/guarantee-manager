package com.marcinsz.backend.mongodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDocument {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime operationTime;
    private LocalDate accountCreationDate;
}
