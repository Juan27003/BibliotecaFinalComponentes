package org.idat.Biblioteca.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.idat.Biblioteca.service.JWTService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {

    // Llave segura y compatible con HS256
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "clave-muy-larga-y-segura-para-jwt-12345678901234567890".getBytes()
    );

    private final long EXPIRATION = 1000 * 60 * 60 * 4; // 4 horas

    //  GENERAR TOKEN

    @Override
    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    //  OBTENER EMAIL
    @Override
    public String obtenerUsuario(String token) {
        return getAllClaims(token).getSubject();
    }

    //  OBTENER ROL

    @Override
    public String obtenerRol(String token) {
        return getAllClaims(token).get("rol", String.class);
    }

    //  VALIDAR TOKEN
    @Override
    public boolean validarToken(String token) {
        try {
            getAllClaims(token); // si falla lanza excepción
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //  OBTENER TOKEN DEL REQUEST

    @Override
    public String obtenerTokenDesdeRequest(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    //  OBTENER ID SI LO GUARDAMOS EN EL TOKEN

    @Override
    public Long getUsuarioIdDesdeToken(String token) {
        Claims claims = getAllClaims(token);
        Object id = claims.get("id");
        return id != null ? Long.parseLong(id.toString()) : null;
    }

    //  VALIDAR SI ES ADMIN

    @Override
    public boolean esAdmin(String token) {
        return "ADMIN".equalsIgnoreCase(obtenerRol(token));
    }

    //  METODO INTERNO
    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
