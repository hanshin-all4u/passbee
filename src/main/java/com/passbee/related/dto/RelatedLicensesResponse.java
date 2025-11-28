package com.passbee.related.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatedLicensesResponse(
        String jmcd, String type, java.util.List<Item> items
) {
    public record Item(String jmcd, String name, BigDecimal score, String note){}
}