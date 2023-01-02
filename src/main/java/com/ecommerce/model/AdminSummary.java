package com.ecommerce.model;

public interface AdminSummary {
    Integer getId();
    String getUsername();
    String getProfilePicture();
    String getProductName();
    String getProductImage();
    Integer getOrderQuantity();
    Double getAmount();
}
