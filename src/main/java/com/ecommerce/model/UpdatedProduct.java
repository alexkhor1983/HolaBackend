package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdatedProduct {
    private String productName;
    private Double productPrice;
    private String productDesc;
    private String productImage;
    private Integer productDiscount;
    private String category;
    private String productEnabled;
    private ArrayList<ProductSpecificationModel> productSpecificationModels;
}
