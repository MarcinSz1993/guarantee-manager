package com.marcinsz.backend.exception;

public class NewsArticleApiException extends RuntimeException {
    public NewsArticleApiException(String message) {
        super(message);
    }
}
