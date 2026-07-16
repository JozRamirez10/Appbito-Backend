package com.appbito.appbito.auth;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;

import com.appbito.appbito.auth.filter.JwtAuthenticationFilter;
import com.appbito.appbito.auth.filter.JwtValidationFilter;
import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.utils.AuthUtils;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Spring Security Config.
 */
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final AuthorizationRules authorizationRules;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${app.cookies.secure}")
    private boolean secureCookie;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        SecretKey secretKey = AuthUtils.stringToSecretKey(jwtSecret);

        return http
            .cors(cors -> cors.configurationSource(configurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(management -> management.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorizationRules)
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), secretKey,
                secureCookie))
            .addFilter(new JwtValidationFilter(authenticationManager(), secretKey))
            .build();
    }

    @Bean
    CorsConfigurationSource configurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(this.allowedOrigins);

        config.setAllowedMethods(Constants.Auth.ALLOWED_METHODS);
        config.setAllowedHeaders(List.of(HEADER_AUTHORIZATION, Constants.Auth.CONTENT_TYPE));
        config.setExposedHeaders(List.of(HEADER_AUTHORIZATION));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(Constants.Auth.ALL_PATHS, config);
        return source;
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
            new CorsFilter(this.configurationSource())
        );
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
}
