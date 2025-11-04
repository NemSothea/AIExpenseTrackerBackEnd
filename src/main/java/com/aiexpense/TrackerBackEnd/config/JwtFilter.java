package com.aiexpense.trackerbackend.config;

import com.aiexpense.trackerbackend.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aiexpense.trackerbackend.service.CustomUserDetailsService;
import com.aiexpense.trackerbackend.service.JwtService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    // private static final Logger logger =
    // LoggerFactory.getLogger(JwtFilter.class);

    private static final java.util.Set<String> BLACKLIST = java.util.Collections
            .synchronizedSet(new java.util.HashSet<>());

    public static void addToBlacklist(String token) {
        BLACKLIST.add(token);
    }

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/auth/")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);

        if (BLACKLIST.contains(token)) {
            write401(res, "Token blacklisted");
            return;
        }

        String username;
        try {
            username = jwtService.extractUserName(token); // from "sub"
        } catch (Exception e) {
            write401(res, "Invalid token");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                if (!jwtService.validateToken(token, user)) {
                    write401(res, "Token invalid or expired");
                    return;
                }
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (UserNotFoundException ex) {
                write401(res, "User not found");
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private void write401(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write("{\"error\":\"" + msg + "\"}");
    }
}
