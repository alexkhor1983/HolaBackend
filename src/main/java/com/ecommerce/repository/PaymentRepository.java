package com.ecommerce.repository;

import com.ecommerce.entity.Payment;
import com.ecommerce.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    @Cacheable
    @Query(value="SELECT COALESCE(SUM(IF(month = 'Jan', total, 0)),0) AS 'January',  COALESCE(SUM(IF(month = 'Feb', total, 0)),0) AS 'February', COALESCE(SUM(IF(month = 'Mar', total, 0)),0) AS 'March',  COALESCE(SUM(IF(month = 'Apr', total, 0)),0) AS 'April',  COALESCE(SUM(IF(month = 'May', total, 0)),0) AS 'May', COALESCE(SUM(IF(month = 'June', total, 0)),0) AS 'June',  COALESCE(SUM(IF(month = 'Jul', total, 0)),0) AS 'July',  COALESCE(SUM(IF(month = 'Aug', total, 0)),0) AS 'August',  COALESCE(SUM(IF(month = 'Sep', total, 0)),0) AS 'September',  COALESCE(SUM(IF(month = 'Oct', total, 0)),0) AS 'October',  COALESCE(SUM(IF(month = 'Nov', total, 0)),0) AS 'November',  COALESCE(SUM(IF(month = 'Dec', total, 0)),0) AS 'December' FROM ( SELECT DATE_FORMAT(payment_date, \"%b\") AS month, SUM(payment_amount) as total FROM payment WHERE payment_date <= NOW() and payment_date >= Date_add(Now(),interval - 12 month) GROUP BY DATE_FORMAT(payment_date, \"%m-%Y\")) as sub",nativeQuery = true)
    SalesSummary getEveryMonthSalesSummary();

    @Query(value = "SELECT order_created.order_id as id, profile.user_profile_picture as userProfile, user.user_name as username, IF(order_created.rate_status IS TRUE,\"true\",\"false\") AS rating, product.product_image as productImg, product.product_name as productName, order_created.order_quantity as quantity, order_created.amount as amount FROM order_created INNER JOIN user ON order_created.user_name = user.user_name INNER JOIN profile ON profile.user_name = user.user_name INNER JOIN product ON order_created.product_id = product.product_id WHERE order_created.user_name = ?1",nativeQuery = true)
    ArrayList<UserTransaction> getTransactionByUsername(String username);

    //Report Function
    @Query(value = "SELECT order_created.order_id as id, product.product_name as productName, product.product_image as productImage, order_created.order_quantity as orderQuantity, (order_created.amount * order_created.order_quantity) AS amount FROM order_created INNER JOIN payment ON order_created.payment_id = payment.payment_id INNER JOIN product ON order_created.product_id = product.product_id ORDER BY payment.payment_date DESC LIMIT 10",nativeQuery = true)
    ArrayList<SummaryTransaction> getTransactionOfNewestTen();

    @Query(value = "SELECT order_created.order_id AS id, product.product_image AS productImage, product.product_name AS productName, order_created.order_quantity AS quantity, product_specification.specification AS specification, order_created.amount AS amount, order_created.user_name AS username, profile.user_profile_picture AS profileImage, payment.payment_date AS paymentDate FROM order_created INNER JOIN product ON order_created.product_id = product.product_id INNER JOIN product_specification ON order_created.prod_spec_id = product_specification.product_spec_id AND product_specification.product_id = product.product_id INNER JOIN product_sale ON product_sale.product_id = product.product_id INNER JOIN payment ON order_created.payment_id = payment.payment_id INNER JOIN user ON order_created.user_name = user.user_name INNER JOIN profile ON profile.user_name = user.user_name WHERE product_sale.user_name = ?1",nativeQuery = true)
    ArrayList<SellerSummary> getAllTransactionFromSeller(String sellerId);

    @Cacheable
    @Query(value = "SELECT order_created.order_id AS id, user.user_name AS username, profile.user_profile_picture AS profilePicture, product.product_name AS productName, product.product_image AS productImage, order_created.order_quantity AS orderQuantity, (order_created.amount * order_created.order_quantity) AS amount FROM order_created INNER JOIN product ON order_created.product_id = product.product_id INNER JOIN user ON order_created.user_name = user.user_name INNER JOIN profile ON profile.user_name = user.user_name",nativeQuery = true)
    ArrayList<AdminSummary> getAllTransactions();
}
