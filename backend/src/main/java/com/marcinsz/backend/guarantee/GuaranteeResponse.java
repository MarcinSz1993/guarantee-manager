package com.marcinsz.backend.guarantee;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class GuaranteeResponse {
    private Long id;
    private String brand;
    private String model;
    private String documentUrl;
    private String notes;
    private Product kindOfProduct;
    private LocalDate startDate;
    private LocalDate endDate;
    private GuaranteeStatus guaranteeStatus;
}
