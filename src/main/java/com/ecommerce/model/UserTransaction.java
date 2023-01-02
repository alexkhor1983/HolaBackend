package com.ecommerce.model;

public interface UserTransaction {
        Integer getId();
        String getUserProfile();
        String getUsername();
        String getRating();
        String getProductImg();
        String getProductName();
        Integer getQuantity();
        Double getAmount();

}
