package com.ecommerce.repository;

import com.ecommerce.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification,Integer> {

    @Query(value ="SELECT product_specification.product_spec_id,product_specification.product_id, product_specification.product_quantity, product_specification.specification,product.product_id,product.product_name,product.product_price, product.product_desc, product.product_image,product.product_discount, product.product_enabled, category.category_id, category.category_name FROM product_specification INNER JOIN product ON product_specification.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id WHERE product_specification.product_id = ?1",nativeQuery = true)
    List<ProductSpecification> findProductSpecificationByProductId(Integer productId);

    @Query(value ="SELECT product_specification.product_spec_id,product_specification.product_id, product_specification.product_quantity, product_specification.specification,product.product_id,product.product_name,product.product_price, product.product_desc, product.product_image,product.product_discount, product.product_enabled, category.category_id, category.category_name FROM product_specification INNER JOIN product ON product_specification.product_id = product.product_id INNER JOIN category ON product.category_id = category.category_id WHERE product_specification.product_id = ?1 AND product_specification.specification = ?2",nativeQuery = true)
    ProductSpecification findProductSpecificationByProductIdAndSpecificationName(Integer productId,String productSpecificationName);
}
