package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import com.marcinsz.backend.guarantee.Product;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GuaranteeHistoryResponse {
    private Long id;
    private String brand;
    private String model;
    private GuaranteeStatus guaranteeStatus;
    private Product kindOfProduct;
    private LocalDateTime changeTime;
    private String notes;
    private boolean positiveFeedback;
}
