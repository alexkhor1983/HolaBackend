package com.ecommerce.model;

public interface SummaryTransaction {
    Integer getId();
    String getProfilePicture();
    String getUserName();
    String getProductImage();
    String getProductName();
    Integer getOrderQuantity();
    Double getAmount();
}
