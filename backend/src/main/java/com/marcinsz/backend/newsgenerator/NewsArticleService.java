package com.marcinsz.backend.newsgenerator;

import com.marcinsz.backend.config.NewsGeneratorApiWebClientConfig;
import com.marcinsz.backend.exception.NewsArticleApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class NewsArticleService {

    private final NewsGeneratorApiWebClientConfig newsGeneratorApiWebClientConfig;

    public NewsArticleRawApi getNewsArticle(String articleParam) {
        return newsGeneratorApiWebClientConfig.newsGeneratorWebClient()
                .get()
                .uri(createFinalUrl(articleParam))
                .retrieve()
                .onStatus(new Predicate<HttpStatusCode>() {
                              @Override
                              public boolean test(HttpStatusCode httpStatusCode) {
                                  return httpStatusCode.isError();
                              }
                          },
                        new Function<ClientResponse, Mono<? extends Throwable>>() {
                            @Override
                            public Mono<? extends Throwable> apply(ClientResponse clientResponse) {
                                return clientResponse.bodyToMono(String.class)
                                        .map(new Function<String, Throwable>() {
                                            @Override
                                            public Throwable apply(String body) {
                                                return new NewsArticleApiException("NewsArticleApi exception: " + body);
                                            }
                                        });
                            }
                        })
                .bodyToMono(NewsArticleRawApi.class)
                .map(newsArticleRawApi -> {
                    List<NewsArticle> listFilteredByImageUrlNotNull = newsArticleRawApi.getResults().stream()
                            .filter(newsArticle -> newsArticle.getImageUrl() != null)
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
