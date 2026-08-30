package com.appbito.appbito.auth;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * Authorization Rules.
 */
public interface AuthorizationRules extends Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>
    .AuthorizationManagerRequestMatcherRegistry> {

}
