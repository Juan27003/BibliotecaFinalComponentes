package org.idat.Biblioteca.service;

import jakarta.servlet.http.HttpServletRequest;

public interface JWTService {

    String generarToken(String username, String rol);

    // Devuelve el email almacenado en el token
    String obtenerUsuario(String token);

    // Devuelve el rol almacenado en el token
    String obtenerRol(String token);

    boolean validarToken(String token);

    String obtenerTokenDesdeRequest(HttpServletRequest request);

    Long getUsuarioIdDesdeToken(String token);

    boolean esAdmin(String token);
}
