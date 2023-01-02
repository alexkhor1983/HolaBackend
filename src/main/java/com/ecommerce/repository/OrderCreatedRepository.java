package com.ecommerce.repository;

import com.ecommerce.entity.OrderCreated;
import com.ecommerce.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface OrderCreatedRepository extends JpaRepository<OrderCreated, Integer> {

    @Query(value = "SELECT order_created.product_id as productId FROM order_created WHERE order_created.order_id = ?1",nativeQuery = true)
    FindProductByOrder findProductIdByOrderId(Integer orderId);

    //used by admin
    @Query(value = "SELECT order_created.user_name AS username, payment.payment_date AS paymentDate, payment.payment_amount AS paymentAmount FROM order_created INNER JOIN payment ON order_created.payment_id = payment.payment_id",nativeQuery = true)
    ArrayList<SummaryTransaction> findSummaryOrderCreated();
}
