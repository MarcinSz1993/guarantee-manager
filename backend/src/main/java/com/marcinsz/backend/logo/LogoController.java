package com.marcinsz.backend.logo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logo")
public class LogoController {

    @GetMapping
    public ResponseEntity<Resource> getLogo(){
        Resource resource = new ClassPathResource("static/images/guarantee-manager-logo.png");
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }
}
