package com.appbito.appbito.repositories;

import com.appbito.appbito.dtos.UserDtoCredentials;
import com.appbito.appbito.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * User Repository.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT new com.appbito.appbito.dtos.UserDtoCredentials( "
        + "u.id, u.email, u.password, u.enabled) FROM User u WHERE u.email = :email")
    Optional<UserDtoCredentials> findCredentialsByEmail(@Param("email") String email);

    Optional<User> findByVerificationToken(String token);

    @Query("SELECT u.verificationToken FROM User u WHERE u.email = :email")
    Optional<String> findVerificationTokenByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM User u WHERE u.id = :id")
    void deleteUserByIdDirectly(@Param("id") Long id);
}
