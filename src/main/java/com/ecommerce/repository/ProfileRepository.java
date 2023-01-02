package com.ecommerce.repository;

import com.ecommerce.entity.Profile;
import com.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Integer> {

    @Query(value = "SELECT profile.profile_id, user.user_name, user.user_role, user.user_password, user.enabled ,user.activated, user.authentication_code,profile.user_phone ,profile.user_email, profile.user_profile_picture FROM profile INNER JOIN user ON profile.user_name = user.user_name WHERE profile.user_name = ?1", nativeQuery=true)
    Optional<Profile> findInfoByUserName(String username);

    @Query(value = "SELECT EXISTS(SELECT profile.user_email FROM profile WHERE profile.user_email = ?1) as truth",nativeQuery = true)
    Integer findByEmail(String email);
}
