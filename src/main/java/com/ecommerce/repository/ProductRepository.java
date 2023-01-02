package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import com.ecommerce.model.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Cacheable
    @Query(value = "SELECT product.product_id as productId, product.product_name as productName, category.category_name as categoryName, ROUND((1 - (product.product_discount / 100)) * product.product_price, 2) AS productPrice, (SELECT ROUND(IFNULL(AVG(rating.rating),0),0) FROM product product LEFT JOIN rating rating ON rating.product_id = product.product_id) AS avgRating, product.product_desc as productDesc, product.product_image as productImg, product_sale.user_name as userName FROM product_sale INNER JOIN product ON product_sale.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id WHERE product.product_enabled = TRUE",nativeQuery = true)
    ArrayList<ProductDetail> findAllEnabledProduct();

    @Query(value = "SELECT product.product_id AS id, product.product_name AS name, category.category_name AS category, ROUND((1 - product.product_discount / 100) * product.product_price, 2) AS price, product.product_image AS img, IF(product.product_enabled IS TRUE,\"Enabled\",\"Disabled\") AS status FROM product INNER JOIN category ON product.category_id = category.category_id INNER JOIN product_sale ON product_sale.product_id = product.product_id WHERE product_sale.user_name = ?1",nativeQuery = true)
    ArrayList<SellerAndAdminProductListModel> findAllProductBySeller(String username);

    @Query(value = "SELECT product.product_name AS productName, product.product_price AS productPrice, product.product_desc AS productDesc, product.product_discount AS productDiscount, category.category_name AS categoryName, IF(product.product_enabled IS TRUE,\"true\",\"false\") AS enabled, product.product_image AS productImg FROM product INNER JOIN category ON product.category_id = category.category_id WHERE product.product_id = ?1",nativeQuery = true)
    SellerProductDetail findSellerEditInfoByProductId(Integer productId);

    @Query(value = "SELECT product.product_id as productId, product.product_name as productName, category.category_name as categoryName, ROUND((1 - (product.product_discount / 100)) * product.product_price, 2) AS productPrice, (SELECT ROUND(IFNULL(AVG(rating.rating),0),0) FROM product product LEFT JOIN rating rating ON rating.product_id = product.product_id  WHERE product.product_id = ?1) AS avgRating, product.product_desc as productDesc, product.product_image as productImg, product_sale.user_name as userName FROM product_sale INNER JOIN product ON product_sale.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id WHERE product.product_enabled = TRUE AND product.product_id = ?1",nativeQuery = true)
    Optional<ProductDetail> findEnabledProductById(Integer id);

    @Query(value = "SELECT product.product_id AS productId, product.product_name AS productName, category.category_name AS categoryName, ROUND((1 - product.product_discount / 100) * product.product_price, 2) AS productPrice, (SELECT ROUND(IFNULL(AVG(rating.rating), 0), 0) AS expr1 FROM product product LEFT OUTER JOIN rating rating ON rating.product_id = product.product_id) AS avgRating, product.product_desc AS productDesc, product.product_image AS productImg, product_sale.user_name AS userName FROM product_sale INNER JOIN product ON product_sale.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id INNER JOIN order_created ON order_created.product_id = product.product_id WHERE order_created.order_id = ?1",nativeQuery = true)
    Optional<ProductDetail> findEnabledProductByOrderId(Integer id);

    @Query(value = "SELECT product_specification.specification as optionName, product_specification.product_quantity as quantityLeft FROM product_specification WHERE product_specification.product_quantity > 0 AND product_specification.product_id = ?1",nativeQuery = true)
    ArrayList<Option> findOptionsById(Integer id);

    @Query(value = "SELECT product.product_id AS id, product.product_name AS productName, product.product_image AS productImg, product.product_price AS productPrice, product.product_discount AS productDiscount, category.category_name AS categoryName, product.product_enabled AS enabled, profile.user_profile_picture AS profileImg, product_sale.user_name AS sellerName FROM product INNER JOIN category ON product.category_id = category.category_id INNER JOIN product_sale ON product_sale.product_id = product.product_id INNER JOIN user ON product_sale.user_name = user.user_name INNER JOIN profile ON profile.user_name = user.user_name",nativeQuery = true)
    ArrayList<AdminProductDetail> getAllProductToAdmin();
}
