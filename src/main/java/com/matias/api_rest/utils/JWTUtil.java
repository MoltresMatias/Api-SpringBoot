package com.matias.api_rest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad para crear y validar JWT
 */
@Component // Permite que Spring administre esta clase y la inyecte con @Autowired
public class JWTUtil {

    @Value("${security.jwt.secret}") // Lee la clave secreta desde application.properties
    private String key;

    @Value("${security.jwt.issuer}") // Lee el emisor del token desde configuración
    private String issuer;

    @Value("${security.jwt.ttlMillis}") // Lee el tiempo de vida del token (Time To Live)
    private long ttlMillis;

    private final Logger log = LoggerFactory.getLogger(JWTUtil.class);

    /**
     * Crear un nuevo token JWT
     */
    public String create(String id, String subject, String rol) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // Prepara la clave secreta para firmar el token
        Key signingKey = new SecretKeySpec(key.getBytes(), signatureAlgorithm.getJcaName());

        // Construye el JWT con ID, fecha, sujeto (email), rol, emisor y firma
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .claim("rol", rol)
                .setIssuer(issuer)
                .signWith(signingKey, signatureAlgorithm);

        // Establece la fecha de expiración si se configuró un TTL
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    /**
     * Obtener el subject del JWT validado
     */
    public String getValue(String jwt) {
        try {                           
            // Decodifica el token y extrae el "subject" (usualmente el email)
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(key.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException e) {
            log.error("Token inválido o expirado: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtener el ID del JWT validado
     */
    public String getKey(String jwt) {
        try {
            // Decodifica el token y extrae el ID del usuario
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(key.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.getId();
        } catch (JwtException e) {
            log.error("Token inválido o expirado: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Obtener el rol del JWT validado
     */
    public String getRol(String jwt) {
        try {
            // Decodifica el token y extrae el rol del usuario
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(new SecretKeySpec(key.getBytes(), SignatureAlgorithm.HS256.getJcaName()))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return claims.get("rol", String.class);
        } catch (JwtException e) {
            log.error("Token inválido o expirado: {}", e.getMessage());
            return null;
        }
    }
}
