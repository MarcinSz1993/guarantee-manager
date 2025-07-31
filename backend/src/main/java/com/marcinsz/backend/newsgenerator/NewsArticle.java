package com.marcinsz.backend.newsgenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class NewsArticle {

    @JsonProperty("article_id")
    public String articleId;
    public String title;
    public String link;
    public String description;
    public String pubDate;
    @JsonProperty("image_url")
    public String imageUrl;

}
