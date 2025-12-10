package org.idat.Biblioteca.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.idat.Biblioteca.dto.PrestamoDTO;
import org.idat.Biblioteca.dto.UsuarioDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.service.PrestamoService;
import org.idat.Biblioteca.service.UsuarioService;
import org.idat.Biblioteca.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private JWTService jwtService; // Para manejar tokens JWT

    @Autowired
    private UsuarioService usuarioService;

    // Crear nuevo préstamo
    @PostMapping
    public ResponseEntity<?> crearPrestamo(@RequestBody PrestamoDTO dto) {
        try {
            PrestamoDTO creado = prestamoService.crearPrestamo(dto);
            return ResponseEntity.status(201).body(Map.of(
                    "message", "Préstamo registrado correctamente",
                    "prestamo", creado
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (RuntimeException e) { // Ejemplo: libro sin stock disponible
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    // Listar todos los préstamos
    @GetMapping
    public ResponseEntity<List<PrestamoDTO>> listarPrestamos() {
        return ResponseEntity.ok(prestamoService.listarPrestamos());
    }

    // Obtener préstamo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPrestamo(@PathVariable Long id) {
        try {
            PrestamoDTO prestamo = prestamoService.obtenerPrestamoPorId(id);
            return ResponseEntity.ok(Map.of("prestamo", prestamo));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Actualizar información de préstamo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPrestamo(@PathVariable Long id, @RequestBody PrestamoDTO dto,
                                                @RequestParam(required = false) Long usuarioId) {
        try {
            PrestamoDTO actualizado = prestamoService.actualizarPrestamo(id, dto, usuarioId);
            return ResponseEntity.ok(Map.of(
                    "message", "Préstamo actualizado correctamente",
                    "prestamo", actualizado
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Devolver un préstamo (marcar como devuelto)
    @PutMapping("/devolver/{id}")
    public ResponseEntity<?> devolverPrestamo(@PathVariable Long id) {
        try {
            PrestamoDTO prestamo = prestamoService.obtenerPrestamoPorId(id);
            prestamo.setFechaDevolucion(new Date()); // Fecha actual de devolución
            prestamo.setEstado("DEVUELTO"); // Cambia estado a DEVUELTO
            PrestamoDTO actualizado = prestamoService.actualizarPrestamo(id, prestamo, prestamo.getUsuarioId());
            return ResponseEntity.ok(Map.of(
                    "message", "Préstamo devuelto correctamente",
                    "prestamo", actualizado
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Eliminar un préstamo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPrestamo(@PathVariable Long id,
                                              @RequestParam(required = false) Long usuarioId) {
        try {
            prestamoService.eliminarPrestamo(id, usuarioId);
            return ResponseEntity.ok(Map.of("message", "Préstamo eliminado correctamente"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    // Obtener préstamos del usuario autenticado
    @GetMapping("/misprestamos")
    public ResponseEntity<?> misPrestamos(HttpServletRequest request) {
        try {
            String token = jwtService.obtenerTokenDesdeRequest(request);
            if (token == null || !jwtService.validarToken(token)) {
                return ResponseEntity.status(401).body(Map.of("message", "Token inválido o no proporcionado"));
            }

            String email = jwtService.obtenerUsuario(token); // Extrae email del token
            UsuarioDTO usuario = usuarioService.obtenerUsuarioPorEmail(email);

            List<PrestamoDTO> prestamos = prestamoService.listarPrestamosPorUsuario(usuario.getId());

            if (prestamos.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "No se encontraron préstamos para el usuario",
                        "prestamos", prestamos
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Préstamos obtenidos correctamente",
                    "prestamos", prestamos
            ));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }

    // Obtener préstamos activos (no devueltos)
    @GetMapping("/activos")
    public ResponseEntity<?> obtenerPrestamosActivos() {
        List<PrestamoDTO> prestamos = prestamoService.obtenerPrestamosActivos();
        if (prestamos.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "message", "No hay préstamos activos en este momento",
                    "prestamos", prestamos
            ));
        }
        return ResponseEntity.ok(Map.of(
                "message", "Préstamos activos obtenidos correctamente",
                "prestamos", prestamos
        ));
    }
}