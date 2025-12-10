package org.idat.Biblioteca.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.idat.Biblioteca.dto.UsuarioDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.service.JWTService;
import org.idat.Biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JWTService jwtService; // Para manejar tokens JWT

    // Obtener información del usuario autenticado
    @GetMapping("/micuenta")
    public ResponseEntity<?> miCuenta(HttpServletRequest request) {
        try {
            String token = jwtService.obtenerTokenDesdeRequest(request); // Extrae token del header Authorization
            if (token == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Token no proporcionado"));
            }

            String email = jwtService.obtenerUsuario(token); // Obtiene email desde el token JWT
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorEmail(email);

            return ResponseEntity.ok(Map.of("usuario", usuario));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    // Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Actualizar usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioDTO actualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Usuario actualizado correctamente",
                    "usuario", actualizado
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Listar todos los usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    // Buscar usuarios por nombre
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarUsuarios(@RequestParam String nombre) {
        try {
            List<UsuarioDTO> usuarios = usuarioService.buscarUsuariosPorNombre(nombre);
            if (usuarios.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "message", "No se encontraron usuarios con ese nombre"
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "message", "Usuarios encontrados correctamente",
                    "usuarios", usuarios
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}