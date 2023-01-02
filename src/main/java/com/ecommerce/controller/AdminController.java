package com.ecommerce.controller;

import com.ecommerce.model.*;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductSaleRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.awt.image.AreaAveragingScaleFilter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewSalesSummary(){
        SalesSummary salesSummary;
        try {
            salesSummary = paymentRepository.getEveryMonthSalesSummary();
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot retrieve dashboard data",HttpStatus.BAD_REQUEST);
        }
        ArrayList<SalesDashboard> salesDashboards = new ArrayList<SalesDashboard>();
        Integer systemMonth = LocalDate.now().getMonthValue();

        salesDashboards.add(new SalesDashboard("January", salesSummary.getJanuary()));

        if(systemMonth >= 2){
            salesDashboards.add(new SalesDashboard("February", salesSummary.getFebruary()));
        }
        if (systemMonth >= 3){
            salesDashboards.add(new SalesDashboard("March", salesSummary.getMarch()));
        }
        if(systemMonth >= 4){
            salesDashboards.add(new SalesDashboard("April", salesSummary.getApril()));
        }
        if(systemMonth >= 5){
            salesDashboards.add(new SalesDashboard("May", salesSummary.getMay()));
        }
        if(systemMonth >= 6){
            salesDashboards.add(new SalesDashboard("June", salesSummary.getJune()));
        }
        if(systemMonth >= 7){
            salesDashboards.add(new SalesDashboard("July", salesSummary.getJuly()));
        }
        if(systemMonth >= 8){
            salesDashboards.add(new SalesDashboard("August", salesSummary.getAugust()));
        }
        if(systemMonth >= 9){
            salesDashboards.add(new SalesDashboard("September", salesSummary.getSeptember()));
        }
        if(systemMonth >= 10){
            salesDashboards.add(new SalesDashboard("October", salesSummary.getOctober()));
        }
        if(systemMonth >= 11){
            salesDashboards.add(new SalesDashboard("November", salesSummary.getNovember()));
        }
        if(systemMonth == 12){
            salesDashboards.add(new SalesDashboard("December", salesSummary.getDecember()));
        }
        return new ResponseEntity<>(salesDashboards, HttpStatus.OK);
    }

    @GetMapping("/user")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewAllUser(){
        ArrayList<UserDetail> users;
        try {
            users = userRepository.getAllUser();
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error ,cannot find user",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{username}")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> deleteByUser(@PathVariable String username){
        try {
            userRepository.deleteById(username);
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error ,cannot find user",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("User delete successfully", HttpStatus.OK);
    }

    @GetMapping("/product")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewAllProduct(){
        ArrayList<AdminProductDetail> adminProductDetails = new ArrayList<>();
        try{
            adminProductDetails = productRepository.getAllProductToAdmin();
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find product details",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(adminProductDetails, HttpStatus.OK);
    }

    @GetMapping("/report/customerConsumeReport")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewCustomerConsumeReport(){
        ArrayList<ReportCustomerConsume> report;
        try{
            report = productSaleRepository.getReportCustomerConsume();
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find data",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(report,HttpStatus.OK);
    }

    @GetMapping("/report/productHotSalesReport")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewProductHotSalesReport(){
        ArrayList<ProductHotSalesReport> report;
        try{
            report = productSaleRepository.getViewProductHotSalesReport();
        }catch(Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find data",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(report,HttpStatus.OK);
    }
}
