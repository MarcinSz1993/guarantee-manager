package com.marcinsz.backend.guarantee;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGuaranteeRequest {
    @NotEmpty(message = "Type brand of the product.")
    private String brand;
    @NotEmpty(message = "Type model of the product.")
    private String model;
    private String notes;
    @NotNull(message = "Please select kind of product.")
    private Product kindOfProduct;
    @FutureOrPresent(message = "Start day cannot be past.")
    @NotNull(message = "Please type start date of the guarantee.")
    private LocalDate startDate;
    @Future(message = "End date cannot be past or present. Only future days are allowed.")
    @NotNull(message = "Please type end date of the guarantee.")
    private LocalDate endDate;
}
