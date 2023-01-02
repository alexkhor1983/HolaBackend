package com.ecommerce.repository;

import com.ecommerce.entity.ProductSale;
import com.ecommerce.model.ProductHotSalesReport;
import com.ecommerce.model.ReportCustomerConsume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface ProductSaleRepository extends JpaRepository<ProductSale,Integer> {

    ProductSale findByProductId(Integer productId);

    //Used by Admin
    @Query(value = "SELECT user.user_name AS id, profile.user_email AS userEmail, profile.user_phone AS userPhone, SUM(order_created.amount) AS totalAmount FROM profile INNER JOIN user ON profile.user_name = user.user_name INNER JOIN order_created ON order_created.user_name = user.user_name GROUP BY user.user_name ORDER BY order_created.amount",nativeQuery = true)
    ArrayList<ReportCustomerConsume> getReportCustomerConsume();

    //Used by Admin
    @Query(value = "SELECT order_created.product_id AS id, product.product_name AS productName, product_sale.user_name AS sellerName, SUM(order_created.order_quantity) AS quantitySales, SUM(order_created.amount) AS amountEarn FROM product_sale INNER JOIN product ON product_sale.product_id = product.product_id INNER JOIN order_created ON order_created.product_id = product.product_id GROUP BY order_created.product_id ORDER BY amountEarn DESC",nativeQuery = true)
    ArrayList<ProductHotSalesReport> getViewProductHotSalesReport();
}
