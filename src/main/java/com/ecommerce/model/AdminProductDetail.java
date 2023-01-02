package com.ecommerce.model;

public interface AdminProductDetail {
        Integer getId();
        String getProductName();
        String getProductImg();
        Double getProductPrice();
        Integer getProductDiscount();
        String getCategoryName();
        String getEnabled();
        String getSellerProfile();
        String getSellerName();
}
