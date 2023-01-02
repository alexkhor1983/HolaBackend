package com.ecommerce.controller;

import javax.annotation.security.RolesAllowed;
import javax.persistence.Column;
import javax.validation.Valid;

import com.ecommerce.api.MailRequest;
import com.ecommerce.entity.Profile;
import com.ecommerce.model.CreateUserFromAdmin;
import com.ecommerce.model.ForgetPassword;
import com.ecommerce.model.Register;
import com.ecommerce.repository.ProfileRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.EmailService;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private Environment env;

	@Autowired
	private UserService uService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@PostMapping("/createUser")
	public ResponseEntity<?> createUser(@RequestBody @Valid Register register) {

		Integer userEmailExistence = profileRepository.findByEmail(register.getEmail());

		if(userEmailExistence == 1){
			return new ResponseEntity<>("Email already exists",HttpStatus.BAD_REQUEST);
		}

		User user = new User(register.getUsername(), "ROLE_USER", register.getPassword(), Boolean.TRUE, Boolean.FALSE, UUID.randomUUID().toString());
		System.out.println(user);
		Profile profile = new Profile(user, register.getEmail(),register.getPhoneNum(), "");
		try {
			uService.createUser(user);
			uService.createProfile(profile);

			Map<String, Object> model = new HashMap<>();
			model.put("uuid", user.getAuthenticationCode());
			model.put("username", user.getUsername());
			model.put ("frontendUrl",env.getProperty("BASE_URL"));

			MailRequest mailRequest = new MailRequest(register.getEmail(), env.getProperty("spring.mail.username"), "Hola Clothes Account Activation");

			emailService.sendEmail(mailRequest, model,"registration");
		} catch (Exception e) {
			System.out.println("Error cause from UserController.java : " + e);
			return new ResponseEntity<>("Error Occur !!! Create user and verification mail sending function occur error", HttpStatus.BAD_REQUEST);
		}
		String res = String.format("User created and verification link was send to email : %s", register.getEmail());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@CrossOrigin
	@GetMapping("/activate/{username}/{uuid}")
	public ResponseEntity<?> activateUser(@PathVariable String username,@PathVariable String uuid){
		User user = uService.findInactiveUserByUsernameAndUuid(username,uuid);

		if (user != null && user.getAuthenticationCode().equals(uuid)) {
			user.setActivated(true);
			try {
				uService.updateUser(user);
			}catch (Exception e) {
				System.out.println(e);
				return new ResponseEntity<>("Failed to update user verification status in database",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Account Activated", HttpStatus.OK);
		}
		return new ResponseEntity<>("Activation failed", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/forgetPassword/{username}")
	public ResponseEntity<?> forgetPassword(@PathVariable String username) {

		User user;
		Profile profile;
		try{
			user = userRepository.findById(username).get();
			profile = profileRepository.findInfoByUserName(username).get();
		}catch (Exception e){
			System.out.println(e);
			return new ResponseEntity<>("Database error, cannot find user",HttpStatus.BAD_REQUEST);
		}

		if (user == null) {
			return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
		}

		user.setAuthenticationCode(UUID.randomUUID().toString());

		Map<String, Object> model = new HashMap<>();
		model.put("uuid", user.getAuthenticationCode());
		model.put("username", user.getUsername());
		model.put ("frontendUrl",env.getProperty("BASE_URL"));

		MailRequest mailRequest = new MailRequest(profile.getUserEmail(), env.getProperty("spring.mail.username"), "Hola Clothes Account Password Reset");
			try {
				emailService.sendEmail(mailRequest, model,"forgetPassword");
				uService.updateUser(user);
			}catch (Exception e) {
				System.out.println(e);
				return new ResponseEntity<>("Failed to send verification email",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Verification Email has send", HttpStatus.OK);
	}

	@PostMapping("/forgetPassword")
	public ResponseEntity<?> resetPassword(@RequestBody ForgetPassword forgetPassword){
		User user = new User();
		if(forgetPassword == null){
			return new ResponseEntity<>("The data should not be empty",HttpStatus.BAD_REQUEST);
		}

		try {
			user = userRepository.findById(forgetPassword.getUsername()).get();
		}catch (Exception e){
			System.out.println(e);
			return new ResponseEntity<>("User cannot found ",HttpStatus.BAD_REQUEST);
		}

		if (user != null && user.getAuthenticationCode().equals(forgetPassword.getUuid())) {
			PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			user.setUserPassword(passwordEncoder.encode(forgetPassword.getPassword()));
			try {
				uService.updateUser(user);
			}catch (Exception e) {
				System.out.println(e);
				return new ResponseEntity<>("Failed to update user password in database",HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Password Changed Successfully", HttpStatus.OK);
		}
		return new ResponseEntity<>("password change failed", HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/createUser/Admin/")
	@RolesAllowed({"ROLE_ADMIN"})
	public ResponseEntity<?> createUserByAdmin(@RequestBody @Valid CreateUserFromAdmin register) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();

		User userLogin = userRepository.findById(currentPrincipalName).get();

		if(userLogin == null) {
			return new ResponseEntity<>("User not found",HttpStatus.BAD_REQUEST);
		}

		if(!userLogin.getUserRole().equals("ROLE_ADMIN")){
			return new ResponseEntity<>("User not authorized",HttpStatus.UNAUTHORIZED);
		}

		Integer userEmailExistence = profileRepository.findByEmail(register.getEmail());

		if(userEmailExistence == 1){
			return new ResponseEntity<>("Email already exists",HttpStatus.BAD_REQUEST);
		}

		User user = new User(register.getUsername(), register.getRole(), register.getPassword(), register.getEnabled(), register.getVerified(), UUID.randomUUID().toString());
        Profile profile = new Profile(user, register.getEmail(),register.getPhoneNum(), register.getProfileImage());

		try
		{
			uService.createUser(user);
			uService.createProfile(profile);
		}
		catch (Exception e) {
			return new ResponseEntity<>("User creation failed", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("user created", HttpStatus.OK);
	}

}
