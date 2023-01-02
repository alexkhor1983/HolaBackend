package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderedSellerProductModel {
    private Integer id;
    private String productImg;
    private String productName;
    private Double productPrice;
    private String categoryName;
}
