package com.ecommerce.controller;

import javax.validation.Valid;

import com.ecommerce.api.AuthRequest;
import com.ecommerce.api.AuthResponse;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.ecommerce.jwt.JwtTokenUtil;
import com.ecommerce.entity.User;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	JwtTokenUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
		try {
			Authentication authentication = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(), request.getPassword())
			);
			User user = userRepository.findById(authentication.getPrincipal().toString()).get();
			String accessToken = jwtUtil.generateAccessToken(user);
			AuthResponse response = new AuthResponse(user.getUsername(), accessToken);
			
			return new ResponseEntity<>(response, HttpStatus.OK);
			
		} catch (BadCredentialsException ex) {
			return new ResponseEntity<>(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
