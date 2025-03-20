package com.marcinsz.backend.guarantee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/guarantee")
@RequiredArgsConstructor
public class GuaranteeController {
    private final GuaranteeService guaranteeService;

    @DeleteMapping
    public ResponseEntity<String> deleteGuarantee(Authentication authentication,
                                                      Long guaranteeId){
        guaranteeService.deleteGuarantee(authentication,guaranteeId);
        return ResponseEntity.accepted().body("You have successfully deleted the guarantee.");
    }

    @PutMapping("/edit-expiration")
    public ResponseEntity<String> editGuaranteeExpiration(Authentication authentication,
                                                      Long guaranteeId,
                                                      LocalDate expirationDate){
        guaranteeService.editGuaranteeExpiration(authentication,guaranteeId,expirationDate);
        return ResponseEntity.accepted().body("You have successfully edited the guarantee expiration date.");
    }

    @PutMapping("/edit-status")
    public ResponseEntity<String> editGuaranteeStatus(Authentication authentication,
                                                      Long guaranteeId,
                                                      GuaranteeStatus guaranteeStatus){
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

    @PostMapping( consumes ={ MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<GuaranteeResponse> addGuarantee(
            Authentication authentication,
            @RequestPart("file") MultipartFile file,
            @RequestParam String brand,
            @RequestParam String model,
            @RequestParam String notes,
            @RequestParam Device kindOfDevice,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) throws IOException {
        AddGuaranteeRequest addGuaranteeRequest = createAddGuaranteeRequest(brand, model, notes, kindOfDevice, startDate, endDate);
        GuaranteeResponse guaranteeResponse = guaranteeService.addGuarantee(authentication,addGuaranteeRequest, file);
        return ResponseEntity.ok(guaranteeResponse);
    }

    private AddGuaranteeRequest createAddGuaranteeRequest(String brand, String model, String notes,
                                                          Device kindOfDevice, LocalDate startDate, LocalDate endDate) {
        return AddGuaranteeRequest.builder()
                .brand(brand)
                .model(model)
                .notes(notes)
                .kindOfDevice(kindOfDevice)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}



