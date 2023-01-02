package com.ecommerce.security;

import com.ecommerce.api.MailRequest;
import com.ecommerce.entity.Profile;
import com.ecommerce.repository.ProfileRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.entity.User;
import com.ecommerce.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findById(userName).orElseThrow(()->
                new UsernameNotFoundException("User not found"));

        if(passwordEncoder.matches(password,user.getUserPassword())){
            if (user.getActivated()){
                if(user.getEnabled()){
                    System.out.println("Current user has the role" + user.getUserRole());
                    System.out.println("user.getAuthorities result : " + user.getAuthorities());
                    return new UsernamePasswordAuthenticationToken(userName,password,user.getAuthorities());
                }else{
                    throw new BadCredentialsException("User is disabled by admin");
                }
            }else{
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("uuid", user.getAuthenticationCode());
                    model.put("username", user.getUsername());

                    Profile profile = profileRepository.findInfoByUserName(user.getUsername()).get();
                    MailRequest mailRequest = new MailRequest(profile.getUserEmail(), env.getProperty("spring.mail.username"), "Hola Clothes Account Activation");
                    emailService.sendEmail(mailRequest, model,"registration");
                    System.out.println(emailService.toString());
                } catch (Exception e) {
                throw new BadCredentialsException("mail sending with error");
            }
                throw new BadCredentialsException("User is not verified, verification mail is resend to the email, please check it and validate account");
            }
        }else{
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
