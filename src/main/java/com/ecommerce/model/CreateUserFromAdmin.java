package com.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateUserFromAdmin {
    String username;
    String password;
    String role;
    Boolean enabled;
    Boolean verified;
    String phoneNum;
    String email;
    String profileImage;
}

