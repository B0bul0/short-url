package me.bruno.shorturl.security;

import lombok.extern.log4j.Log4j2;
import me.bruno.shorturl.service.APIAuthKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Log4j2
@Configuration
public class APISecurityConfig {

    private static final String PRINCIPAL_REQUEST_HEADER = "API-Key";

    @Autowired
    private APIAuthKeyService apiAuthKeyService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        APIKeyAuthFilter filter = new APIKeyAuthFilter(PRINCIPAL_REQUEST_HEADER);
        filter.setAuthenticationManager(authentication -> {
            String key = (String) authentication.getPrincipal();

            if (key == null || key.isEmpty()) {
                throw new BadCredentialsException("The API key was not found or not the expected value. (Empty)");
            }

            boolean validateKey = apiAuthKeyService.validateKey(key);

            if (!validateKey) {
                throw new BadCredentialsException("The API key was not found or not the expected value.");
            }

            authentication.setAuthenticated(true);
            return authentication;
        });

        httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").authenticated() // Require authentication for API
                        .anyRequest().permitAll() // Allow all other requests
                )
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Disable session creation
                .addFilter(filter) // Add API key auth filter
        ;

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager noopAuthenticationManager() {
        return authentication -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }

}