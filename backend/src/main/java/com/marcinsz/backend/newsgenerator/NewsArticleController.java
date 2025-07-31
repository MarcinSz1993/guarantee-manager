package com.marcinsz.backend.newsgenerator;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article-generator")
@RequiredArgsConstructor
public class NewsArticleController {

    private final NewsArticleService newsArticleService;

    @GetMapping
    public NewsArticleRawApi getNewsArticles(@RequestParam String articleParam) {

            return newsArticleService.getNewsArticle(articleParam);
        }
    }

