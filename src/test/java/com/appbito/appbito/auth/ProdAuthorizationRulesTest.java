package com.appbito.appbito.auth;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.appbito.appbito.auth.filter.JwtValidationFilter;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

/**
 * Prod Authorization Rules Test.
 */
@WebMvcTest(
    controllers = ProdAuthorizationRulesTest.DummyController.class,
    properties = {
        "server.port=8080",
        "jwt.secret=LlaV3FaL5aDePru3bas1234567890qwertyuiopasdfghjkl",
        "app.cookies.secure=true"
    }
)
@Import({SecurityConfig.class, ProdAuthorizationRules.class})
@ActiveProfiles("prod")
class ProdAuthorizationRulesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtValidationFilter jwtValidationFilter;

    private static final String GET_API_USERS = "/api/users";

    @Test
    void rulesCoverage() {

        ProdAuthorizationRules rules = new ProdAuthorizationRules();

        AuthorizeHttpRequestsConfigurer<HttpSecurity>
            .AuthorizationManagerRequestMatcherRegistry authz =
                mock(
                    AuthorizeHttpRequestsConfigurer.
                        AuthorizationManagerRequestMatcherRegistry.class,
                    Answers.RETURNS_DEEP_STUBS
                );

        rules.customize(authz);
    }

    @Test
    void prodRulesDenyAll() throws Exception {

        mockMvc.perform(
            get(GET_API_USERS)
        )
        .andExpect(status().isUnauthorized());
    }

    @RestController
    static class DummyController { }
}
