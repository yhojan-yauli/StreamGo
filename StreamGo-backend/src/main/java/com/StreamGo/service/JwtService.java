package com.StreamGo.service;

import com.StreamGo.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // clave segura
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // GENERAR TOKEN
    public String generateToken(Usuario usuario) {

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // EXTRAER CLAIMS (REUTILIZABLE)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // EMAIL DEL TOKEN
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ROL DEL TOKEN
    public String extractRole(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    // VALIDAR EXPIRACIÓN
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // VALIDAR TOKEN
    public boolean isTokenValid(String token, String email) {
        try {
            Claims claims = extractAllClaims(token);

            String username = claims.getSubject();

            return username.equals(email) && !isTokenExpired(claims);

        } catch (Exception e) {
            return false;
        }
    }
}