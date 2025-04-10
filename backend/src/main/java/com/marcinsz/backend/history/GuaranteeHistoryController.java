package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/guarantee-history")
@RequiredArgsConstructor
public class GuaranteeHistoryController {

    private final GuaranteeHistoryService guaranteeHistoryService;

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteGuaranteeHistory(@RequestParam Long guaranteeHistoryId) {
        guaranteeHistoryService.deleteGuaranteeHistory(guaranteeHistoryId);
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully deleted")
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addGuaranteeChange(Authentication connectedUser, @Valid CreateGuaranteeHistoryRequest createGuaranteeHistoryRequest){
        guaranteeHistoryService.addGuaranteeChange(connectedUser, createGuaranteeHistoryRequest);
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully added guarantee change.")
                .build());
    }

    @GetMapping
    public ResponseEntity<Page<GuaranteeHistoryResponse>> getAllUserGuaranteesHistories(Authentication connectedUser,
                                                                                        @RequestParam(defaultValue = "0") int pageNumber,
                                                                                        @RequestParam(defaultValue = "10") int pageSize){
        return ResponseEntity.ok().body(guaranteeHistoryService.getAllUserGuaranteesHistories(connectedUser, pageNumber, pageSize));
    }

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
