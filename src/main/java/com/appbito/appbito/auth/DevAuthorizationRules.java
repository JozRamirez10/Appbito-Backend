package com.appbito.appbito.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Dev Authorization Rules.
 */
@Component
@Profile({"dev", "test"})
public class DevAuthorizationRules implements AuthorizationRules {

    @Override
    public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>
        .AuthorizationManagerRequestMatcherRegistry authz) {

        authz
            .requestMatchers(HttpMethod.POST, "/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/habits").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.GET, "/auth/verify").permitAll()
            .requestMatchers(HttpMethod.GET, "/test/auth/**").permitAll()
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                .permitAll()
            .anyRequest().authenticated();

    }

}
