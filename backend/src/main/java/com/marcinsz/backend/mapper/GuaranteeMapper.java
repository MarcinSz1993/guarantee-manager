package com.marcinsz.backend.mapper;

import com.marcinsz.backend.guarantee.Guarantee;
import com.marcinsz.backend.guarantee.GuaranteeResponse;

public class GuaranteeMapper {

    public static GuaranteeResponse mapGuaranteeToGuaranteeResponse(Guarantee guarantee) {
        return GuaranteeResponse.builder()
                .id(guarantee.getId())
                .brand(guarantee.getBrand())
                .model(guarantee.getModel())
                .documentUrl(guarantee.getDocumentUrl())
                .notes(guarantee.getNotes())
                .kindOfProduct(guarantee.getKindOfProduct())
                .startDate(guarantee.getStartDate())
                .endDate(guarantee.getEndDate())
                .guaranteeStatus(guarantee.getGuaranteeStatus())
                .build();

    }


}
