package com.marcinsz.backend.newsgenerator;

import com.marcinsz.backend.config.NewsGeneratorApiWebClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsGeneratorApiWebClientConfig newsGeneratorApiWebClientConfig;

    public NewsArticleRawApi getNewsArticle(String articleParam) {
        return newsGeneratorApiWebClientConfig.newsGeneratorWebClient()
                .get()
                .uri(createFinalUrl(articleParam))
                .retrieve()
                .bodyToMono(NewsArticleRawApi.class)
                .map(newsArticleRawApi -> {
                    List<NewsArticle> listFilteredByImageUrlNotNull = newsArticleRawApi.getResults().stream()
                            .filter(newsArticle -> newsArticle.getImage_url() != null)
                            .toList();
                    newsArticleRawApi.setResults(listFilteredByImageUrlNotNull);
                    newsArticleRawApi.setTotalResults(listFilteredByImageUrlNotNull.size());
                    return newsArticleRawApi;
                })
                .block();
    }

    private Function<UriBuilder, URI> createFinalUrl(String articleParam) {
        return uriBuilder -> uriBuilder
                .queryParam("q", articleParam)
                .queryParam("country", "pl")
                .queryParam("language", "pl")
                .build();
    }
}
