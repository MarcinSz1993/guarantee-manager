package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guarantee-history")
@RequiredArgsConstructor
public class GuaranteeHistoryController {

    private final GuaranteeHistoryService guaranteeHistoryService;

    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getAllByGuaranteeStatus(@RequestParam GuaranteeStatus guaranteeStatus,
                                                               Authentication connectedUser) {
        return ResponseEntity.ok().body(ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(guaranteeHistoryService.getAllByGuaranteeStatus(guaranteeStatus,connectedUser).toString())
                .build());
    }

    @GetMapping("/feedback")
    public ResponseEntity<ApiResponse> getAllPositiveFeedbacks(@RequestParam Boolean positiveFeedback, Authentication connectedUser) {
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(guaranteeHistoryService.getAllFeedbacks(positiveFeedback,connectedUser).toString())
                .build());
    }
}
