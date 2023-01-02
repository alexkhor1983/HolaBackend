package com.ecommerce.controller;

import com.ecommerce.entity.Profile;
import com.ecommerce.entity.User;
import com.ecommerce.model.Password;
import com.ecommerce.model.ProfileRequest;
import com.ecommerce.repository.ProfileRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @GetMapping("/getUserInfo")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> getUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        User userLogin = userRepository.findById(currentPrincipalName).get();

        if(userLogin == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }

        Profile profile;
        ProfileRequest request = new ProfileRequest();

        try {
            profile = profileRepository.findInfoByUserName(currentPrincipalName).get();
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find user profile",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        request.setUsername(currentPrincipalName);
        request.setEmail(profile.getUserEmail());
        request.setProfileImg(profile.getUserProfilePicture());
        request.setPhoneNum(profile.getUserPhone());
        return new ResponseEntity<>(request,HttpStatus.OK);
    }

    @GetMapping("/getUserInfo/getProfileImg/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username){
        Profile profile;
        ProfileRequest request = new ProfileRequest();

        try {
            profile = profileRepository.findInfoByUserName(username).get();
        }catch(Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot find user profile",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        request.setProfileImg(profile.getUserProfilePicture());
        return new ResponseEntity<>(request,HttpStatus.OK);
    }

    @PostMapping("/updateUserInfo")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> updateUserDetails(@RequestBody ProfileRequest profileRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User userLogin;

        try {
            userLogin = userRepository.findById(currentPrincipalName).get();
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot search user", HttpStatus.BAD_REQUEST);
        }

        if(userLogin == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }

        Profile profile = profileRepository.findInfoByUserName(currentPrincipalName).get();
        Pattern pattern = Pattern.compile("^(01)[02-46-9]-*[0-9]{7}$|^(01)[1]-*[0-9]{8}$");
        Matcher matcher = pattern.matcher(profileRequest.getPhoneNum());

        if(!matcher.matches()){
            return new ResponseEntity<>("Invalid phone number", HttpStatus.BAD_REQUEST);
        }else{
            profile.setUserPhone(profileRequest.getPhoneNum());
        }

        profile.setUserProfilePicture(profileRequest.getProfileImg());

        try {
            profileRepository.save(profile);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Update failed in database", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Profile updated", HttpStatus.OK);
    }

    @PostMapping("/editPassword")
    @RolesAllowed({"ROLE_USER","ROLE_ADMIN"})
    public ResponseEntity<?> updateUserPassword(@RequestBody Password password){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User userLogin;

        try {
            userLogin = userRepository.findById(currentPrincipalName).get();
        } catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot search user", HttpStatus.BAD_REQUEST);
        }

        if(userLogin == null) {
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userLogin.setUserPassword(passwordEncoder.encode(password.getPassword()));
        try{
            userRepository.save(userLogin);
        }catch (Exception e){
            System.out.println(e);
            return new ResponseEntity<>("Database error, cannot update password",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Password updated", HttpStatus.OK);
    }

}
