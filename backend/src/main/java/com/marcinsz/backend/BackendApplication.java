package com.marcinsz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}

//todo 1. Zmienić api w newsdata.io tak, aby pokazywało artykuły tylko z kategorii TECHNOLOGY
//todo 2. Zmienić nagłówek z Last News na coś w stylu Articles that might interest you...
//todo 3. Stworzyć feature(scrapper), który będzie czytał artykuł ze strony docelowej,
//todo a następnie zapisywał te dane do PDFa.
//todo 4. Pod każdym z 3 artykułów dodać przycisk z możliwością pobrania tego PDFa. Uzytkownik będzie miał więc możliwość
//todo przeczytać artykuł bezpośrednio na stronie lub przez pobranie PDFa.
//todo 5. Zmodyfikować backend i frontend tak, aby nie możliwe było dodać historii gwarancji ze sprzecznymi danymi np.
//todo ma nie być możliwości dodać historii gwarancji ze statusem REJECTED i positive feedback TRUE
//todo lub PENDING tak, aby nie można było dodać ani TRUE ani FALSE do positive feedback. Zastanowić się czy
//todo jest sens zmienić architekturę i jak tak to zrobić to.