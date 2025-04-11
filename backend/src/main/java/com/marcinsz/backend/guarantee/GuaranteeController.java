package com.marcinsz.backend.guarantee;

import com.marcinsz.backend.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/guarantee")
@RequiredArgsConstructor
public class GuaranteeController {
    private final GuaranteeService guaranteeService;

    @GetMapping("/duration")
    public ResponseEntity<ApiResponse> getAverageDurationOfGuarantee(Authentication connectedUser,
                                                                     @RequestParam Product kindOfProduct) {
        Integer averageDurationOfGuarantee = guaranteeService.getAverageDurationOfGuarantee(connectedUser, kindOfProduct);
        return ResponseEntity.ok().body(ApiResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("An average guarantee duration in " + kindOfProduct + " is " + averageDurationOfGuarantee + " days.")
                .build());
    }

    @GetMapping("/most-popular")
    public ResponseEntity<ApiResponse> getTheMostPopularKindOfProduct(Authentication connectedUser) {
        return ResponseEntity.ok().body(ApiResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .message(guaranteeService.getTheMostPopularKindOfProduct(connectedUser))
                .build());
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteGuarantee(Authentication authentication,
                                                      @RequestParam Long guaranteeId){
        guaranteeService.deleteGuarantee(authentication,guaranteeId);
        return ResponseEntity.accepted().body(ApiResponse.builder()
                        .statusCode(HttpStatus.ACCEPTED.value())
                        .message("You have successfully deleted the guarantee.")
                .build());
    }

    @PutMapping("/edit-expiration")
    public ResponseEntity<String> editGuaranteeExpiration(Authentication authentication,
                                                      @RequestParam Long guaranteeId,
                                                      @RequestParam LocalDate expirationDate){
        guaranteeService.editGuaranteeExpiration(authentication,guaranteeId,expirationDate);
        return ResponseEntity.accepted().body("You have successfully edited the guarantee expiration date.");
    }

    @PutMapping("/edit-status")
    public ResponseEntity<String> editGuaranteeStatus(Authentication authentication,
                                                      @RequestParam Long guaranteeId,
                                                      @RequestParam GuaranteeStatus guaranteeStatus){
        guaranteeService.editGuaranteeStatus(authentication,guaranteeId,guaranteeStatus);
        return ResponseEntity.accepted().body("You have successfully edited the guarantee status to " + guaranteeStatus.name());
    }

    @GetMapping("/{guaranteeId}")
    public ResponseEntity<GuaranteeResponse> getGuaranteeDetails(@PathVariable Long guaranteeId,Authentication authentication) {
        return ResponseEntity.ok().body(guaranteeService.getGuaranteeDetails(guaranteeId,authentication));
    }

    @GetMapping
    public ResponseEntity<Page<GuaranteeResponse>> getAllUserGuarantees(Authentication authentication,
                                                                        @RequestParam(name = "page",defaultValue = "0")int page,
                                                                        @RequestParam(name = "size",defaultValue = "6")int size) {
        return ResponseEntity.ok(guaranteeService.getAllGuarantees(authentication,page,size));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GuaranteeResponse> addGuarantee(
            Authentication authentication,
            @RequestPart("file") MultipartFile file,
            @RequestPart("data") @Valid AddGuaranteeRequest addGuaranteeRequest
    ) throws IOException {
        GuaranteeResponse guaranteeResponse = guaranteeService.addGuarantee(authentication, addGuaranteeRequest, file);
        return ResponseEntity.ok(guaranteeResponse);
    }
}



