package com.marcinsz.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ValidationErrorsResponse
{
    private int statusCode;
    private Map<String,String> errors;
}
