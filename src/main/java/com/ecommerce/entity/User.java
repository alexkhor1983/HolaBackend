package com.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class User implements UserDetails {

    public User(String username){
        this.id = username;
    }

    @Id
    @Column(name = "user_name", nullable = false, length = 50)
    private String id;

    @Column(name = "user_role", nullable = false, length = 10)
    private String userRole;

    @Column(name = "user_password", nullable = false, length = 100)
    private String userPassword;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "activated", nullable = false)
    private Boolean activated = false;

    @Column(name = "authentication_code", length = 100)
    private String authenticationCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole));
        // Business Logic only allowed either ROLE_USER or ROLE_ADMIN

        return authorities;
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}