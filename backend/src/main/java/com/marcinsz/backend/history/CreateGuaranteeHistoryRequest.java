package com.marcinsz.backend.history;

import com.marcinsz.backend.guarantee.GuaranteeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
public class CreateGuaranteeHistoryRequest {
    @NotNull(message = "Select guarantee.")
    @Positive(message = "Select guarantee.")
    Long guaranteeId;
    @NotNull(message = "Select status.")
    private GuaranteeStatus guaranteeStatus;
    @NotEmpty(message = "The note should have between 1 and 45 characters.")
    @NotBlank(message = "The note should have between 1 and 45 characters.")
    @Length(min = 1, max = 45,message = "The note should have between 1 and 45 characters.")
    private String notes;
    @NotNull(message = "Select if current feedback is positive or not.")
    private Boolean positiveFeedback;
}
