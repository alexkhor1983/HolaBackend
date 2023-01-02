package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Data
@NoArgsConstructor
@ToString
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "profile_id" , nullable = false)
    private Integer profileId;

    @JsonManagedReference
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_name", nullable = false)
    private User user;

    @Column(name = "user_phone", nullable = false, length = 12)
    private String userPhone;

    @Column(name = "user_email", nullable = false, length = 100, unique = true)
    private String userEmail;

    @Column(name = "user_profile_picture", length = 100)
    private String userProfilePicture;


    public Profile(User user,String email, String phoneNum, String userProfilePicture) {
        this.user = user;
        this.userPhone = phoneNum;
        this.userEmail = email;
        this.userProfilePicture = userProfilePicture;
    }
}