package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CartItem {
    private Integer productId;
    private String productName;
    private String productImg;
    private Double productPrice;
    private String option;
    private Integer cartQuantity;

}
