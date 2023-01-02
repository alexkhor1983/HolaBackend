package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateProduct {
    private String productName;
    private Double productPrice;
    private String productDesc;
    private String productImage;
    private Integer productDiscount;
    private Boolean productEnabled;
    private String category;
    private List<ProductSpecificationModel> productSpecificationModels;
}
