package com.ecommerce.init;

import com.ecommerce.entity.*;
import com.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
public class DatabaseInitializer {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LikesListRepository likesListRepository;

    @Autowired
    private OrderCreatedRepository orderCreatedRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSaleRepository productSaleRepository;

    @Autowired
    private ProductSpecificationRepository productSpecificationRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        try {
            userRepository.save(new User("holaadmin", "ROLE_ADMIN", encoder.encode("1234holaecommerce"), true, true, "imtheadmin"));
            userRepository.save(new User("holauser", "ROLE_USER", encoder.encode("1234holaecommerce"), true, true, "imtheseller"));
            userRepository.save(new User("alexkhor12345", "ROLE_USER", encoder.encode("1234holaecommerce"), true, true, "imtheuser"));

            Profile profileAdmin = new Profile();
            profileAdmin.setUser(userRepository.findById("holaadmin").get());
            profileAdmin.setUserPhone("01025478965");
            profileAdmin.setUserEmail("holaadmin123@gmail.com");
            profileAdmin.setUserProfilePicture("");

            profileRepository.save(profileAdmin);

            Profile profileUser = new Profile();
            profileUser.setUser(userRepository.findById("holauser").get());
            profileUser.setUserPhone("01025478965");
            profileUser.setUserEmail("holauser123@gmail.com");
            profileUser.setUserProfilePicture("https://holaclothes-ecommerce-bucket.s3.ap-southeast-1.amazonaws.com/Ilv4aJt02y7kd4y1eYfH6.jpeg");

            profileRepository.save(profileUser);

            Profile profileUser1 = new Profile();
            profileUser1.setUser(userRepository.findById("alexkhor12345").get());
            profileUser1.setUserPhone("01025478965");
            profileUser1.setUserEmail("alexkhor12345@gmail.com");
            profileUser1.setUserProfilePicture("https://holaclothes-ecommerce-bucket.s3.ap-southeast-1.amazonaws.com/zvlmw0XoZ72NBuIVW-M8B.jpeg");

            profileRepository.save(profileUser1);

            categoryRepository.save(new Category(1,"Blouse"));
            categoryRepository.save(new Category(2,"T-Shirt"));
            categoryRepository.save(new Category(3,"Jacket"));
            categoryRepository.save(new Category(4,"Skirt"));
            categoryRepository.save(new Category(5,"Shorts"));
            categoryRepository.save(new Category(6,"Jeans"));
            categoryRepository.save(new Category(7,"Formal Wear"));

            Category category1 = categoryRepository.findById(7).get();
            Product product1 = new Product();
            product1.setId(1);
            product1.setProductName("Regular Fit Tuxedo");
            product1.setProductPrice(600.00);
            product1.setProductDesc("Add a stylish formalwear staple to your wardrobe with our black tuxedo suit. Designed for a classic yet comfortable shape, our two piece suit is perfect for comfort and style. Maintain a smart look throughout the day with our Supercrease™ technology that gives the suit trousers a permanent crease for a fresh look wear after wear.");
            product1.setProductImage("https://asset1.cxnmarksandspencer.com/is/image/mands/Black-Regular-Fit-Tuxedo-Jacket/SD_03_T15_8021_Y0_X_EC_90?$PDP_SETS_PDT_LG$");
            product1.setProductDiscount(0);
            product1.setProductEnabled(true);
            product1.setCategory(category1);

            productRepository.save(product1);

            ProductSpecification productSpecification1 = new ProductSpecification();
            productSpecification1.setId(1);
            productSpecification1.setProduct(product1);
            productSpecification1.setSpecification("S");
            productSpecification1.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification1);

            ProductSpecification productSpecification2 = new ProductSpecification();
            productSpecification2.setId(2);
            productSpecification2.setProduct(product1);
            productSpecification2.setSpecification("XS");
            productSpecification2.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification2);

            ProductSpecification productSpecification3 = new ProductSpecification();
            productSpecification3.setId(3);
            productSpecification3.setProduct(product1);
            productSpecification3.setSpecification("M");
            productSpecification3.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification3);

            ProductSpecification productSpecification4 = new ProductSpecification();
            productSpecification4.setId(4);
            productSpecification4.setProduct(product1);
            productSpecification4.setSpecification("XL");
            productSpecification4.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification4);

            ProductSpecification productSpecification5 = new ProductSpecification();
            productSpecification5.setId(5);
            productSpecification5.setProduct(product1);
            productSpecification5.setSpecification("XXL");
            productSpecification5.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification5);

            ProductSale productSale1 = new ProductSale();
            productSale1.setId(1);
            productSale1.setProduct(product1);
            productSale1.setProductCreateDate(LocalDate.now());
            productSale1.setUser(userRepository.findById("holauser").get());
            productSaleRepository.save(productSale1);

            Category category2 = categoryRepository.findById(1).get();
            Product product2 = new Product();
            product2.setId(2);
            product2.setProductName("Printed High Neck Long Sleeve Blouse");
            product2.setProductPrice(52.00);
            product2.setProductDesc("Crinkle-effect fabric in a printed design gives this blouse a fun flourish. Cut in a regular fit, with a button-down placket and frill detail at the high neck for a feminine feel. The long sleeves have neat cuffs for a tidy look. Made with sustainably sourced viscose. M&S Collection: easy-to-wear wardrobe staples that combine classic and contemporary styles.");
            product2.setProductImage("https://asset1.cxnmarksandspencer.com/is/image/mands/Printed-High-Neck-Long-Sleeve-Blouse-3/SD_01_T41_6020W_Z4_X_EC_1?$PDP_MAIN_CAR_SM$&fmt=webp");
            product2.setProductDiscount(0);
            product2.setProductEnabled(true);
            product2.setCategory(category2);

            productRepository.save(product2);

            ProductSpecification productSpecification6 = new ProductSpecification();
            productSpecification6.setId(6);
            productSpecification6.setProduct(product2);
            productSpecification6.setSpecification("S");
            productSpecification6.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification6);

            ProductSpecification productSpecification7 = new ProductSpecification();
            productSpecification7.setId(7);
            productSpecification7.setProduct(product2);
            productSpecification7.setSpecification("XS");
            productSpecification7.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification7);

            ProductSpecification productSpecification8 = new ProductSpecification();
            productSpecification8.setId(8);
            productSpecification8.setProduct(product2);
            productSpecification8.setSpecification("M");
            productSpecification8.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification8);

            ProductSpecification productSpecification9 = new ProductSpecification();
            productSpecification9.setId(9);
            productSpecification9.setProduct(product2);
            productSpecification9.setSpecification("XL");
            productSpecification9.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification9);

            ProductSpecification productSpecification10 = new ProductSpecification();
            productSpecification10.setId(10);
            productSpecification10.setProduct(product2);
            productSpecification10.setSpecification("XXL");
            productSpecification10.setProductQuantity(20);
            productSpecificationRepository.save(productSpecification10);

            ProductSale productSale2 = new ProductSale();
            productSale2.setId(2);
            productSale2.setProduct(product2);
            productSale2.setProductCreateDate(LocalDate.now());
            productSale2.setUser(userRepository.findById("holauser").get());
            productSaleRepository.save(productSale2);

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
