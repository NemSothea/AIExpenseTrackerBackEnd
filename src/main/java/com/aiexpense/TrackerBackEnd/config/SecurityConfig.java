package com.aiexpense.trackerbackend.config;

/**
 * 1. Purpose:
- Stateless Authentication: JWTs enable stateless authentication, where the server does not need to store session information. Each request contains the necessary authentication information within the JWT.
- API Security: It provides a secure way to protect REST APIs by ensuring that only requests with valid and authorized JWTs can access protected resources.
- Decoupled Authentication: It separates the authentication mechanism from traditional session-based approaches, making it suitable for microservices architectures and mobile applications.

2. Functionality:
- Intercepting Requests: The JWT filter, typically extending OncePerRequestFilter, intercepts every incoming HTTP request.
- Extracting JWT: It extracts the JWT from the Authorization header (usually in the "Bearer" token format).
- Validating JWT: It validates the extracted JWT, performing checks such as:
Signature Verification: Ensuring the token's integrity using the secret key.
- Expiration Check: Verifying that the token has not expired.
- Issuer and Audience Validation: Optionally checking the token's issuer and intended audience.
- User Authentication: If the JWT is valid, the filter extracts the user's identity (e.g., username) from the token's claims. It then loads the corresponding user details (e.g., roles, authorities) and creates an Authentication object.
- Setting Security Context: The Authentication object is then set in the Spring Security SecurityContextHolder, making the user authenticated for the current request.
Filter Chain Progression: After processing, the filter passes the request to the next filter in the Spring Security chain or directly to the controller if it's the last filter.

3. Integration with Spring Security:
- SecurityFilterChain: The JWT filter is added to the SecurityFilterChain configuration, typically before the UsernamePasswordAuthenticationFilter, to ensure it processes the JWT before Spring Security attempts other authentication methods.
- AuthenticationManager: The filter interacts with the AuthenticationManager to authenticate the user based on the extracted JWT information.
- UserDetailsService: It utilizes a UserDetailsService to load user details based on the username extracted from the JWT.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aiexpense.trackerbackend.service.CustomOAuth2UserService;
import com.aiexpense.trackerbackend.service.CustomUserDetailsService;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private final JwtFilter jwtFilter;

    @Autowired
    private final CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    public SecurityConfig(JwtFilter jwtFilter,
            CustomOAuth2UserService customOAuth2UserService,
            OAuth2LoginSuccessHandler oauth2LoginSuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Configuring CustomUserDetailsService bean.");
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Configuring BCryptPasswordEncoder bean.");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.info("Configuring AuthenticationManager bean.");
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.info("Configuring DaoAuthenticationProvider bean.");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring HTTP Security filters.");
        return http
                .cors(c -> c.configurationSource(req -> {
                    var cfg = new org.springframework.web.cors.CorsConfiguration();
                    cfg.setAllowedOrigins(java.util.List.of("http://localhost:5173"));
                    cfg.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    cfg.setAllowedHeaders(java.util.List.of("*")); // includes Authorization
                    cfg.setExposedHeaders(java.util.List.of("*"));
                    cfg.setAllowCredentials(true);
                    return cfg;
                }))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // .requestMatchers(
                        // "/",
                        // "/auth/signup",
                        // "/auth/login",
                        // "/login/oauth2",
                        // "/error",
                        // "/api/expenses/**",
                        // "/api/categories/**",
                        // "api/dashboard/**",
                        // "/swagger-ui/**",
                        // "/v3/api-docs/**"
                        // ).permitAll()

                        .requestMatchers(
                                "/welcome",
                                "/auth/login", "/auth/signup",
                                "/login/oauth2/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/error")
                        .permitAll()

                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(ui -> ui.userService(customOAuth2UserService))
                        .successHandler(oauth2LoginSuccessHandler))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\":\"Forbidden\"}");
                        }))
                // ⬇️ make sure the JWT filter runs before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS Configuration.");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Replace with your frontend URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.info("CORS Configuration successfully set.");
        return source;
    }
}