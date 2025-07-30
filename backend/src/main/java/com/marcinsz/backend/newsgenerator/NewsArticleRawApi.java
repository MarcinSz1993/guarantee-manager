package com.marcinsz.backend.newsgenerator;

import lombok.Data;

import java.util.List;

@Data
public class NewsArticleRawApi {

    private String status;
    private int totalResults;
    private List<NewsArticle> results;

}
