package com.ecommerce.repository;

import java.util.ArrayList;
import java.util.Optional;

import com.ecommerce.entity.User;
import com.ecommerce.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findById(String id);

    @Query(value = "SELECT user.user_name, user.user_role, user.user_password, user.enabled ,user.activated, user.authentication_code FROM user WHERE user.user_name = ?1 AND user.authentication_code = ?2 AND user.activated = FALSE",nativeQuery = true)
    Optional<User> findUnactivatedUserByUsernameAndUuid(String username, String uuid);

    @Query(value = "SELECT profile.profile_id AS id, profile.user_profile_picture AS profileImg, user.user_name AS username, profile.user_email AS userEmail, profile.user_phone AS userPhone,IF(user.enabled IS TRUE,\"true\",\"false\") AS enabled, IF(user.activated IS TRUE,\"true\",\"false\") AS activated FROM profile INNER JOIN user ON profile.user_name = user.user_name WHERE user.user_role = 'ROLE_USER'",nativeQuery = true)
    ArrayList<UserDetail> getAllUser();
}
