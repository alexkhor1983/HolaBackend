package com.ecommerce.controller;

import com.ecommerce.entity.LikesList;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.LikeListExistence;
import com.ecommerce.model.ProductDetail;
import com.ecommerce.repository.LikesListRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.LikeListService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/likeList")
public class LikeListController {

    @Autowired
    private LikesListRepository likesListRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeListService likeListService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> viewLikeList(){
        User user;
        try {
            user = userService.getUserByAuthentication();
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
        }

        List<ProductDetail> likesList = new ArrayList<>();
        try {
            likesList = likesListRepository.findLikeListProductByUserName(user.getUsername());
        }catch (Exception e) {
            System.out.println();
        }
        return new ResponseEntity<>(likesList, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping("/checkLike/{productId}")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> checkIsLike(@PathVariable Integer productId){
        User user;
        try {
            user = userService.getUserByAuthentication();
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
        }
        return new ResponseEntity<>(likeListService.checkIsLike(user.getUsername(),productId), HttpStatus.OK);
    }

    @GetMapping ("/{productId}")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> createOrRemoveLikeList(@PathVariable Integer productId){
        User user;
        Product product;
        try {
            user = userService.getUserByAuthentication();
        }catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.OK);
        }

        try{
            product = productRepository.findById(productId).get();
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error : cannot find product",HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(product == null){
            return new ResponseEntity<>("product not found", HttpStatus.BAD_REQUEST);
        }

        Integer likeListExistence = likesListRepository.checkLikeListExistences(user.getUsername(),productId);
        if(likeListExistence == 0) {

            LikesList likeList = new LikesList();
            likeList.setProduct(product);
            likeList.setUserName(user);

            try {
                likesListRepository.save(likeList);
            }catch (Exception e){
                System.out.println(e);
                return new ResponseEntity<>("Database Error : Cannot Add Product to LikeList",HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Item added to like list", HttpStatus.OK);
        }else {
            LikesList likeList = likesListRepository.findLikesListByUserNameAndProduct(user, product);
            likesListRepository.delete(likeList);

            return new ResponseEntity<>("Item removed from like list", HttpStatus.OK);
        }
    }
}
