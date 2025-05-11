package com.marcinsz.backend;

import com.marcinsz.backend.mongodb.GuaranteeHistoryDocument;
import com.marcinsz.backend.mongodb.GuaranteeHistoryMongoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(GuaranteeHistoryMongoRepository repository) {
        return args -> {
            var guaranteeHistory = GuaranteeHistoryDocument.builder()
                    .guaranteeId(2L)
                    .notes("test")
                    .positiveFeedback(true)
                    .build();
            repository.insert(guaranteeHistory);
        };
    }
}
