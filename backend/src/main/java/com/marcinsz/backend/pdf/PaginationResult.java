package com.marcinsz.backend.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

public record PaginationResult(float currentTextPosition, PDPageContentStream contentStream) {
}
