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

/**
 * Servicio de generación y validación de tokens JWT.
 * Maneja creación, extracción de datos y validación de autenticación.
 *
 * @author Yhojan Yauli
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Genera una clave segura para firmar el token JWT.
     *
     * @return clave de firma HMAC
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     *
     * @param usuario usuario autenticado
     * @return token JWT generado
     */
    public String generateToken(Usuario usuario) {

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    /**
     * Genera un token JWT para un usuario autenticado mediante OAuth2 (Google).
     * (ESTE ES EL QUE NECESITA TU CUSTOMOAUTH2SUCCESSHANDLER)
     *
     * @param email email del usuario obtenido del proveedor
     * @param rol rol asignado en formato String (ej: "CLIENTE")
     * @return token JWT generado
     */
    public String generateTokenFromOAuth2(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * @param token token JWT
     * @return claims contenidos en el token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae el email (username) del token JWT.
     *
     * @param token token JWT
     * @return email del usuario
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae el rol del usuario desde el token JWT.
     *
     * @param token token JWT
     * @return rol del usuario
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param claims datos del token
     * @return true si está expirado, false si es válido
     */
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    /**
     * Valida un token JWT comparando email y expiración.
     *
     * @param token token JWT
     * @param email email esperado del usuario
     * @return true si el token es válido, false si no
     */
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
