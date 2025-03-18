package com.marcinsz.backend.guarantee;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGuaranteeRequest {
    @NotBlank
    @NotEmpty
    private String brand;
    private String model;
    private String notes;
    @NotBlank
    @NotEmpty
    private Device kindOfDevice;
    @FutureOrPresent
    private LocalDate startDate;
    @Future
    private LocalDate endDate;
}
