package com.appbito.appbito.auth.filter;

import static com.appbito.appbito.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.appbito.appbito.auth.TokenJwtConfig.PREFIX_TOKEN;

import com.appbito.appbito.constants.Constants;
import com.appbito.appbito.utils.AuthUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Jwt Validation Filter.
 */
public class JwtValidationFilter extends BasicAuthenticationFilter {

    private final SecretKey secretKey;

    private final JwtParser jwtParser;

    /**
     * method constructor.
     *
     * @param authenticationManager authentication manager.
     * @param secretKey secret key.
     */
    public JwtValidationFilter(AuthenticationManager authenticationManager, SecretKey secretKey) {
        super(authenticationManager);

        this.secretKey = secretKey;
        this.jwtParser = Jwts.parser()
            .verifyWith(this.secretKey)
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(HEADER_AUTHORIZATION);

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(PREFIX_TOKEN.length());

        try {
            Claims claims = this.jwtParser
                .parseSignedClaims(token)
                .getPayload();

            String userId = claims.getSubject();

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                    userId, null, List.of()); // Without roles

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            chain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {

            Map<String, String> body = Map.of(
                Constants.Errors.ERROR, Constants.Errors.UNAUTHORIZED,
                Constants.General.MESSAGE, Constants.Exceptions.INVALID_TOKEN
            );

            AuthUtils.setUnauthorizedResponse(response, body);
        }
    }

}
