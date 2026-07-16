package com.appbito.appbito.services;

import com.appbito.appbito.auth.models.CustomUserDetails;
import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.dtos.UserDtoCredentials;
import com.appbito.appbito.exceptions.AccountNotVerifiedException;
import com.appbito.appbito.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Jpa Details Service.
 */
@Service
@RequiredArgsConstructor
public class UserJpaDetailsService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * load user credentials from database and constructs a CustomUserDetails object.
     *
     * @param email identifyng the user.
     *
     * @return userDetails.
     *
     * @throws UsernameNotFounException if the user could not be found.
     * @throws AccountNotVerifiedException if the user account is not enabled.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserDtoCredentials user = repository.findCredentialsByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(Constants.Errors.USER_NOT_FOUND));

        if (!user.enabled()) {
            throw new AccountNotVerifiedException(Constants.Exceptions.USER_NOT_ENABLED);
        }

        return new CustomUserDetails(user.id(), user.email(), user.password());
    }
}
