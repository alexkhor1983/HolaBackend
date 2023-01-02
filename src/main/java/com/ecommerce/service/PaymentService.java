package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductSale;
import com.ecommerce.entity.ProductSpecification;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.ProductDetail;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductSaleRepository;
import com.ecommerce.repository.ProductSpecificationRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentService {

    @Value("${BASE_URL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    @Autowired
    private ProductSaleRepository productSalesRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSpecificationRepository productSpecificationRepository;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public Session createSession(List<CartItem> cartItemList) throws StripeException {

        // success and failure urls

        String successURL = baseURL + "/payment";

        String failureURL = baseURL + "/payment/failed";

        Stripe.apiKey = apiKey;

        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();

        for (CartItem CartItem: cartItemList) {
            sessionItemList.add(createSessionLineItem(CartItem));
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failureURL)
                .setShippingAddressCollection(
                        SessionCreateParams.ShippingAddressCollection
                                .builder()
                                .addAllowedCountry(
                                        SessionCreateParams.ShippingAddressCollection.AllowedCountry.MY
                                )
                                .addAllowedCountry(
                                        SessionCreateParams.ShippingAddressCollection.AllowedCountry.SG
                                )
                                .build()
                )
                .addAllLineItem(sessionItemList)

                .setSuccessUrl(successURL+"/success/{CHECKOUT_SESSION_ID}")
                .build();
        return Session.create(params);
    }

    private SessionCreateParams.LineItem createSessionLineItem(CartItem CartItem) {

        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(CartItem))
                .setQuantity(Long.parseLong(String.valueOf(CartItem.getCartQuantity())))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(CartItem CartItem) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("myr")
                .setUnitAmount((long)(CartItem.getProductPrice()*100))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(CartItem.getProductName())
                                .addImage(CartItem.getProductImg())
                                .build()
                ).build();
    }

    public Boolean validateCartItem(String username,List<CartItem> cartItemList){
        for (int i = 0; i < cartItemList.size() ; i++){
            ProductSale productSale = productSalesRepository.findByProductId(cartItemList.get(i).getProductId());

            //validate is seller purchase the product itself and existence of the product
            if(productSale.getUser().getUsername().equals(username)) { // seller cannot purchase their product
                System.out.println("Line 110 : seller cannot purchase their product");
                return false;
            }

            //validate is product in the cart
            Product product = productRepository.findById(productSale.getProduct().getId()).get();

            Double priceAfterMultiplyQuantity = 0.0;

            if(product.getProductDiscount() > 0) {
                System.out.println("Line 120 : calculate discount price");
                priceAfterMultiplyQuantity = cartItemList.get(i).getCartQuantity() * (product.getProductPrice() * (1 - ((double)product.getProductDiscount()/100.0)));
            }else{
                System.out.println("Line 123 : use the default price");
                priceAfterMultiplyQuantity = cartItemList.get(i).getCartQuantity() * product.getProductPrice();
                System.out.println("System amount :" + priceAfterMultiplyQuantity);
            }

            Double cartPrice = cartItemList.get(i).getCartQuantity() * cartItemList.get(i).getProductPrice();
            System.out.println("CartItem amount :" + priceAfterMultiplyQuantity);

            if(!Objects.equals(df.format(priceAfterMultiplyQuantity), df.format(cartPrice))){
                System.out.println("Line 130 : not match amount cartItem and amount calculate by backend");
                return false;
            }
            List<ProductSpecification> productSpecifications = productSpecificationRepository.findProductSpecificationByProductId(cartItemList.get(i).getProductId());
            for (int j = 0; j < productSpecifications.size(); j++){
                if(cartItemList.get(i).getOption().equals(productSpecifications.get(j).getSpecification())){
                    System.out.println("Line 136 : matched options found");
                    if(productSpecifications.get(j).getProductQuantity() - cartItemList.get(i).getCartQuantity() < 0 ){
                        System.out.println("Line 138 : product quantity lower than 0 after calculation");
                        return false;
                    }
                }
            }
        }
        return true;
    }


}
