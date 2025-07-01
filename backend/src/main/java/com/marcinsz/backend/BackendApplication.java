package com.marcinsz.backend;

import com.marcinsz.backend.pdf.PdfTest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {ConfigurableApplicationContext context = SpringApplication.run(BackendApplication.class, args);

    }
}