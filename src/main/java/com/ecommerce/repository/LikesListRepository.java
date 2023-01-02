package com.ecommerce.repository;

import com.ecommerce.entity.LikesList;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.model.LikeListExistence;
import com.ecommerce.model.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesListRepository extends JpaRepository<LikesList, Integer> {

    @Query(value = "SELECT COUNT(*) FROM likes_list WHERE likes_list.user_name = ?1 AND likes_list.product_id = ?2",nativeQuery = true)
    Integer checkLikeListExistences(String username,Integer productId);

    LikesList findLikesListByUserNameAndProduct(User userName, Product product);

    @Query(value="SELECT product.product_id AS productId, product.product_name AS productName, category.category_name AS categoryName, ROUND((1 - product.product_discount / 100) * product.product_price, 2) AS productPrice, (SELECT ROUND(IFNULL(AVG(rating.rating), 0), 0) AS expr1 FROM product product LEFT OUTER JOIN rating rating ON rating.product_id = product.product_id) AS avgRating, product.product_desc AS productDesc, product.product_image AS productImg, product_sale.user_name AS userName FROM product_sale INNER JOIN product ON product_sale.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id INNER JOIN likes_list ON likes_list.product_id = product.product_id WHERE product.product_enabled = TRUE AND likes_list.user_name = ?1",nativeQuery = true)
    List<ProductDetail> findLikeListProductByUserName(String userName);
}
