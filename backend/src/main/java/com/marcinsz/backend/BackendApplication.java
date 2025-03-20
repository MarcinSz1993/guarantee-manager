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

    //todo 1. Przetestować zabezpieczone endpointy w pryzpadku nieważnego tokena.
    //todo 3. Poszukać czy przed kontrolerem nie występują jeszcze jakieś rzucenia wyjątków
    //todo i jak, to obsłużyć podobnie jak JwtExpiredException()
    //todo 4. Jeżeli będzie wena to testować na bieżąco każdą metodę.
    //todo 5. Połączyć te metody z frontendem.
}
