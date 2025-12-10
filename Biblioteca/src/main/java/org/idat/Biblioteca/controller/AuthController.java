package org.idat.Biblioteca.controller;

import org.idat.Biblioteca.dto.UsuarioDTO;
import org.idat.Biblioteca.service.JWTService;
import org.idat.Biblioteca.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JWTService jwtService;

    public AuthController(UsuarioService usuarioService, JWTService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    // Registro de nuevos usuarios
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTO usuarioDTO) {
        try {
            usuarioDTO.setRolId(2L); // Asigna rol USUARIO por defecto

            UsuarioDTO usuarioCreado = usuarioService.crearUsuario(usuarioDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario registrado correctamente");
            response.put("id", usuarioCreado.getId());
            response.put("nombre", usuarioCreado.getNombre());
            response.put("email", usuarioCreado.getEmail());
            response.put("rolNombre", usuarioCreado.getRolNombre());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", "El email ya está registrado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error al registrar usuario"));
        }
    }

    // Login de usuarios existentes
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioDTO loginRequest) {
        try {
            // Valida credenciales
            UsuarioDTO usuarioValido = usuarioService.validarUsuario(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );

            // Genera token JWT
            String token = jwtService.generarToken(usuarioValido.getEmail(), usuarioValido.getRolNombre());

            Map<String, Object> response = new HashMap<>();
            response.put("nombre", usuarioValido.getNombre());
            response.put("email", usuarioValido.getEmail());
            response.put("rolNombre", usuarioValido.getRolNombre());
            response.put("token", token); // Token para autenticar peticiones

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error al iniciar sesión"));
        }
    }
}