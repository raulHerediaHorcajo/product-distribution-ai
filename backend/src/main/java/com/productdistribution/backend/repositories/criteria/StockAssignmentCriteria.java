package com.productdistribution.backend.repositories.criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAssignmentCriteria {
    private String storeId;
    private String warehouseId;
    private String productId;
}