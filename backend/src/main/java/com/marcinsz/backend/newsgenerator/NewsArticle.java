package com.marcinsz.backend.newsgenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class NewsArticle {

    @JsonProperty("article_id")
    public String article_id;
    public String title;
    public String link;
    public String description;
    public String pubDate;
    public String image_url;

}
