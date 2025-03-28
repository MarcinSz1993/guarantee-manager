package com.marcinsz.backend.notification.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final GuaranteeExpirationEmailService guaranteeExpirationEmailService;

    @PostMapping
    public ResponseEntity<String> test(){
        guaranteeExpirationEmailService.sendGuaranteeExpirationEmail();
        return ResponseEntity.ok("Email Sent");
    }

}
