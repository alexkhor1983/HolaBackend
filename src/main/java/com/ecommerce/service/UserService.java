package com.ecommerce.service;

import com.ecommerce.entity.Profile;
import com.ecommerce.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProfileRepository profileRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User createUser(User user) {
		String rawPassword = user.getPassword();
		String encodedPassword = passwordEncoder.encode(rawPassword);
		user.setUserPassword(encodedPassword);

		return userRepository.save(user);
	}

	public User getUserByAuthentication () throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentPrincipalName = authentication.getName();

		User user;

		try {
			user = userRepository.findById(currentPrincipalName).get();
		} catch (Exception e){
			System.out.println(e);
			throw new Exception("Database error, cannot search user");
		}

		if(user == null) {
			throw new Exception("User not found");
		}

		if(!user.getActivated() || !user.getEnabled()){
			throw new Exception("User is not activated or disabled");
		}

		return user;
	}

	public User updateUser(User user){
		return userRepository.save(user);
	}

	public void createProfile(Profile profile){
		profileRepository.save(profile);
	}

	public User findInactiveUserByUsernameAndUuid(String username, String uuid){
		return userRepository.findUnactivatedUserByUsernameAndUuid(username,uuid).orElse(null);
	}
}
