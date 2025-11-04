package com.aiexpense.trackerbackend.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import com.aiexpense.trackerbackend.repo.UserRepository;

@Service
public class JwtService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtService.class);

    private final UserRepository userRepo;

    public JwtService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Value("${app.jwt.secret}")
    private String secretBase64; // Base64, >= 32 bytes
    @Value("${app.jwt.ttl:PT24H}")
    private java.time.Duration ttl;

    private javax.crypto.SecretKey signingKey;

    @jakarta.annotation.PostConstruct
    void init() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretBase64);
        if (keyBytes.length < 32) {
            log.warn("JWT secret < 256 bits ({} bytes). HS256 needs >=32 bytes.", keyBytes.length);
        }
        signingKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        String role = userRepo.findRoleByUsername(username);
        if (role == null || role.isBlank())
            role = "ROLE_CUSTOMER";

        var now = java.time.Instant.now();
        var exp = now.plus(ttl);

        return io.jsonwebtoken.Jwts.builder()
                .claims(java.util.Map.of("role", role))
                .subject(username)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(exp))
                .signWith(signingKey, io.jsonwebtoken.Jwts.SIG.HS256) // 0.12.6 API
                .compact();
    }

    public String extractUserName(String token) {
        return claims(token).getSubject();
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails user) {
        try {
            var c = claims(token); // verifies signature & dates
            if (!user.getUsername().equals(c.getSubject()))
                return false;
            var exp = c.getExpiration();
            return exp != null && exp.after(new java.util.Date());
        } catch (Exception e) {
            return false;
        }
    }

    private io.jsonwebtoken.Claims claims(String token) {
        return io.jsonwebtoken.Jwts.parser()
                .verifyWith(signingKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
