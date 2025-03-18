package com.marcinsz.backend.guarantee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/guarantee")
@RequiredArgsConstructor
public class GuaranteeController {
    private final GuaranteeService guaranteeService;

    @PostMapping( consumes ={ MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuaranteeResponse> addGuarantee(
            Authentication authentication,
            @RequestPart("file") MultipartFile file,
            @RequestParam String request
            ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        AddGuaranteeRequest addGuaranteeRequest = objectMapper.readValue(request, AddGuaranteeRequest.class);

        GuaranteeResponse guaranteeResponse = guaranteeService.addGuarantee(authentication, addGuaranteeRequest, file);
        return ResponseEntity.ok(guaranteeResponse);
    }
}
