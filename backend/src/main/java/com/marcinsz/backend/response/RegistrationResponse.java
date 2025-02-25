package com.marcinsz.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegistrationResponse {
    private int statusCode;
    private String message;
}
