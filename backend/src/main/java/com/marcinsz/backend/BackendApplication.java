package com.marcinsz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

//todo 1.Dodać 10 rekordów do tabeli users
//todo 1.Dodać 10 rekordów do tabeli guarantee
//todo 1.Dodać 10 rekordów do tabeli guarantee_history
}
