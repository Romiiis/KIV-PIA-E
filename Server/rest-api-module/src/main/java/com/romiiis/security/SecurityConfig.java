package com.romiiis.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ExecutionContextFilter executionContextFilter;
    private final SecurityRulesLoader rulesLoader;

    /**
     * Security filter chain configured based on external security rules.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(executionContextFilter, JwtAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {

                    for (SecurityRulesLoader.SecurityRule rule : rulesLoader.getRules()) {

                        String path = rule.getPath();
                        String methodStr = rule.getMethod();
                        HttpMethod method = null;

                        // Parsing HTTP method
                        if (methodStr != null && !methodStr.isBlank()) {
                            try {
                                method = HttpMethod.valueOf(methodStr.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                log.warn("Invalid HTTP method '{}' in security rule for path '{}'. Skipping method.", methodStr, path);
                            }
                        }

                        // Building matcher
                        var matcher = (method != null)
                                ? auth.requestMatchers(method, path)
                                : auth.requestMatchers(path);

                        if ("permitAll".equalsIgnoreCase(rule.getAccess())) {
                            matcher.permitAll();
                            log.debug("permitAll {} {}", methodStr, path);
                        } else if (rule.getRoles() != null && !rule.getRoles().isEmpty()) {
                            matcher.hasAnyRole(rule.getRoles().toArray(new String[0]));
                            log.debug("roles {} {} -> {}", methodStr, path, rule.getRoles());
                        } else {
                            matcher.authenticated();
                            log.debug("authenticated {} {}", methodStr, path);
                        }
                    }

                    // Deny any request not matched by above rules
                    auth.anyRequest().denyAll();
                })
                .build();
    }

    /**
     * Basic CORS config
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
