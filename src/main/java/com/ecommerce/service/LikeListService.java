package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.LikesListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class LikeListService {

    @Autowired
    private UserService userService;

    @Autowired
    LikesListRepository likesListRepository;

    public boolean checkIsLike(String username, Integer productId){

        if(username.equals("")){
            return false;
        }
        Integer isLikes = 0;
        try {
            isLikes = likesListRepository.checkLikeListExistences(username,productId);
        }catch (Exception e) {
            System.out.println(e);
            return false;
        }
        if(isLikes == 0){
            return false;
        }
        return true;
    }
}
