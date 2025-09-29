package com.aiexpense.trackerbackend.config;

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

import org.springframework.security.config.http.SessionCreationPolicy;
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
    private JwtFilter jwtFilter;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

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
                .cors(cors -> {
                    logger.info("Configuring CORS with allowed origins and methods.");
                    cors.configurationSource(corsConfigurationSource());
                })
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request

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

                        // ✅ Public endpoints
                        .requestMatchers(
                                "/welcome",
                                "/api/**",
                                "/auth/signup",
                                "/auth/login",
                                "/login/oauth2/**",
                                "/error",
                                // Swagger (optional: dev-only)
                                "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()

                        // ✅ Admin-only (Further secured in service layer)
                        // .requestMatchers(
                        //         "/auth/admin/**")
                        // .hasRole("ADMIN")

                        // ✅customer-only (Currently secured in service layer)
                        // .requestMatchers(
                        //         "/auth/customer/**")
                        // .hasRole("CUSTOMER")

                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oauth2LoginSuccessHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Proper 401/403 handling for REST
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
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String token = request.getHeader("Authorization");
                            if (token != null && token.startsWith("Bearer ")) {
                                token = token.substring(7);
                                JwtFilter.addToBlacklist(token);
                                logger.info("Token has been blacklisted after logout.");
                            }
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().write("{\"message\":\"Logout Successful\"}");
                            response.getWriter().flush();
                        }))
                // ✅ JWT filter (reads Bearer token and populates SecurityContext)
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