package com.ecommerce.controller;

import com.ecommerce.api.MailRequest;
import com.ecommerce.api.StripeResponse;
import com.ecommerce.entity.*;
import com.ecommerce.model.*;
import com.ecommerce.repository.*;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.PaymentService;
import com.ecommerce.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private Environment env;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSpecificationRepository productSpecificationRepository;

    @Autowired
    private OrderCreatedRepository orderCreatedRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @PostMapping("/create-checkout-session")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> checkoutList(@RequestBody List<CartItem> cartItemList) throws StripeException {

        User user;
        try {
            user = userService.getUserByAuthentication();
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
        }

        if(!paymentService.validateCartItem(user.getUsername(), cartItemList)){
            return new ResponseEntity<>("Cart item data is not valid, please clear the cart and add to cart again",HttpStatus.BAD_REQUEST);
        }

        Session session = paymentService.createSession(cartItemList);
        StripeResponse stripeResponse = new StripeResponse(session.getUrl());
        return new ResponseEntity<>(stripeResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/createPaymentHistory/{sessionId}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> createPaymentHistory(@PathVariable String sessionId,@RequestBody ArrayList<CartItem> cartItem) throws StripeException {
        if (sessionId != ""){

            //custom model
            ProductDetail productDetail = null;
            Payment createdPayment = new Payment();

            //Entity model
            Payment payment = new Payment();

            List<ProductSpecification> productSpecification = new ArrayList<>();
            List<OrderCreated> orderCreatedList = new ArrayList<>();

            User user;
            try {
                user = userService.getUserByAuthentication();
            }catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
            }

            if(user.getEnabled() && user.getActivated()){
                Stripe.apiKey = apiKey;

                //create a session and retrieve create session key from Stripe
                Session session = Session.retrieve(sessionId);

                //set the payment information to payment object variable
                payment.setPaymentDate(LocalDate.now());
                payment.setStripeId(sessionId);
                payment.setShippingAddress(session.getCustomerDetails().getAddress().getLine1()+","+session.getCustomerDetails().getAddress().getLine2()+","+session.getCustomerDetails().getAddress().getState()+","+session.getCustomerDetails().getAddress().getPostalCode()+","+session.getCustomerDetails().getAddress().getCountry());
                payment.setPaymentAmount(Double.valueOf((Integer.parseInt(session.getAmountTotal().toString())))/100);

                for (int i = 0; i < cartItem.size(); i++) {
                    System.out.println("In the loop");

                    Product product = new Product();
                    OrderCreated orderCreated = new OrderCreated();

                    //In the loop search the products by product id
                    try {
                        System.out.println("try catch 1");
                        productDetail = productRepository.findEnabledProductById(cartItem.get(i).getProductId()).get();
                    }catch (Exception e){
                        System.out.println(e);
                        return new ResponseEntity<>("Database error, cannot find product",HttpStatus.BAD_REQUEST);
                    }

                    //check existence of product
                    if(productDetail == null){
                        return new ResponseEntity<>("Product cannot found in database",HttpStatus.BAD_REQUEST);
                    }else{
                        //exist then store to product object variable
                        product = productRepository.findById(productDetail.getProductId()).get();
                    }

                    try {
                        //get all options (specification) from product and store to productSpecification object variable, it is a arrayList
                        productSpecification.add(productSpecificationRepository.findProductSpecificationByProductIdAndSpecificationName(product.getId(), cartItem.get(i).getOption()));
                    }catch (Exception e){
                        System.out.println(e);
                        return new ResponseEntity<>("Database error, cannot find options of product",HttpStatus.BAD_REQUEST);
                    }

                    if(productSpecification.get(i) == null) {
                        return new ResponseEntity<>("Option not found", HttpStatus.BAD_REQUEST);
                    }

                    if(productSpecification.get(i).getProductQuantity() - cartItem.get(i).getCartQuantity() < 0){
                        return new ResponseEntity<>("Quantity not enough", HttpStatus.BAD_REQUEST);
                    }else{
                        productSpecification.get(i).setProductQuantity(productSpecification.get(i).getProductQuantity() - cartItem.get(i).getCartQuantity());
                    }

                    orderCreated.setProductId(product);
                    orderCreated.setUserName(user);
                    orderCreated.setProdSpec(productSpecification.get(i));
                    orderCreated.setAmount(cartItem.get(i).getProductPrice());
                    orderCreated.setOrderQuantity(cartItem.get(i).getCartQuantity());
                    orderCreated.setRateStatus(false);
                    orderCreatedList.add(orderCreated);
                    }
                ProductSale productSale;
                String sellerUserEmail;
                try{
                   productSale =  productSaleRepository.findByProductId(productDetail.getProductId());
                   sellerUserEmail = profileRepository.findInfoByUserName(productSale.getUser().getUsername()).get().getUserEmail();
                    System.out.println("Line 178 : The product checkout is success with previous validation, start to send seller email : " + sellerUserEmail + " as a notice");
                }catch (Exception e){
                    return new ResponseEntity<>("Database error, cannot find the product detail",HttpStatus.INTERNAL_SERVER_ERROR);
                }

                //Insert all the data to database
                try{
                    createdPayment = paymentRepository.save(payment);


                    for (int i = 0; i < orderCreatedList.size(); i++){
                        orderCreatedList.get(i).setPayment(createdPayment);
                    }
                    orderCreatedRepository.saveAll(orderCreatedList);
                    productSpecificationRepository.saveAll(productSpecification);

                    for(int i = 0 ; i < orderCreatedList.size() ; i++){
                        Map<String, Object> model = new HashMap<>();

                        Profile profile = profileRepository.findInfoByUserName(orderCreatedList.get(i).getUserName().getUsername()).get();
                        model.put("paymentDate",payment.getPaymentDate());
                        model.put("shipppingAddress",payment.getShippingAddress());
                        model.put("amount", orderCreatedList.get(i).getAmount());
                        model.put ("frontendUrl",env.getProperty("BASE_URL"));

                        MailRequest mailRequest = new MailRequest(profile.getUserEmail(), env.getProperty("spring.mail.username"), "Hola Clothes Seller Have Payment Reminder");
                        try {
                            emailService.sendEmail(mailRequest, model,"sendNotificationToSeller");
                            userService.updateUser(user);
                        }catch (Exception e) {
                            System.out.println(e);
                            return new ResponseEntity<>("Failed to send verification email",HttpStatus.BAD_REQUEST);
                        }
                    }
                }catch (Exception e){
                    System.out.println(e);
                    return new ResponseEntity<>("Database error,cannot create payment and order",HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }else{
                return new ResponseEntity<>("User account is inactivated or disabled, please contact for help",HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("Order record created", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Invalid Session Id", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/ratingOrder/{orderId}/{rating}")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> ratingOrder(@PathVariable Integer orderId,@PathVariable Integer rating) {
        User user;
        try {
            user = userService.getUserByAuthentication();
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
        }

        OrderCreated order = orderCreatedRepository.findById(orderId).get();

        if(order == null) {
            return new ResponseEntity<>("Order not found",HttpStatus.BAD_REQUEST);
        }

        if (rating < 1 || rating > 5){
            return new ResponseEntity<>("Maximum rating of the order is minimum 1, maximum 5",HttpStatus.BAD_REQUEST);
        }

        order.setRateStatus(true);

        try{
            orderCreatedRepository.save(order);

        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot save the rating status",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        FindProductByOrder findProductByOrder = orderCreatedRepository.findProductIdByOrderId(orderId);
        Product product;
        try{
            product = productRepository.findById(findProductByOrder.getProductId()).get();
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find the product by order",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Rating ratingRepo = new Rating();
        ratingRepo.setRating(rating);
        ratingRepo.setProduct(product);
        ratingRepo.setOrderCreated(order);

        try{
            ratingRepository.save(ratingRepo);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot save the rating status",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Order rated",HttpStatus.OK);
    }

    @GetMapping("/viewHistory")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> viewPaymentHistory() {
        User user;
        try {
            user = userService.getUserByAuthentication();

        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        ArrayList<UserTransaction> transactions;

        try{
            transactions = paymentRepository.getTransactionByUsername(user.getUsername());
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(e,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(transactions,HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/viewAllHistory")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewAllPaymentHistory() {
        ArrayList<AdminSummary> summaryTransaction = new ArrayList<>();
        try {
            summaryTransaction = paymentRepository.getAllTransactions();
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database Error, cannot find transaction",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(summaryTransaction,HttpStatus.OK);
    }

    @GetMapping("/viewAllHistory/newTen")
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<?> viewNewestTenPaymentHistory() {
        ArrayList<SummaryTransaction> summaryTransaction = new ArrayList<>();
        try {
            summaryTransaction = paymentRepository.getTransactionOfNewestTen();
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database Error, cannot find transaction",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(summaryTransaction,HttpStatus.OK);
    }

    @GetMapping("/viewHistory/seller")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> viewAllPaymentHistoryRelatedWithSeller() {
        User user;
        try {
            user = userService.getUserByAuthentication();

        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        ArrayList<SellerSummary> transactionRelateWithSeller;
        try {
            transactionRelateWithSeller = paymentRepository.getAllTransactionFromSeller(user.getUsername());
        }catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Database error , cannot find seller transaction record",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(transactionRelateWithSeller,HttpStatus.OK);
    }
}
