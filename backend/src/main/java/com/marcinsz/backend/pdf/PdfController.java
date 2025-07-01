package com.marcinsz.backend.pdf;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<byte[]> getUserLogsPdf(@RequestParam String userEmail) throws IOException {

        byte[] userLogsPdfDocument = pdfService.createUserLogsPdfDocument(userEmail);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("doc.pdf").build());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(userLogsPdfDocument);

    }
}
