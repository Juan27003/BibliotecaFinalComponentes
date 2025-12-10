package org.idat.Biblioteca.controller;

import org.idat.Biblioteca.dto.LibroDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @PostMapping
    public ResponseEntity<?> crearLibro(@RequestBody LibroDTO libroDTO) {
        LibroDTO creado = libroService.crearLibro(libroDTO);
        return ResponseEntity.status(201).body(Map.of(
                "message", "Libro creado correctamente",
                "libro", creado
        ));
    }

    @GetMapping
    public ResponseEntity<List<LibroDTO>> listarLibros() {
        // Devuelve todos los libros
        return ResponseEntity.ok(libroService.listarLibros());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerLibro(@PathVariable Long id) {
        try {
            LibroDTO libro = libroService.obtenerLibroPorId(id);
            return ResponseEntity.ok(Map.of("libro", libro));
        } catch (ResourceNotFoundException e) {
            // Libro no encontrado
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody LibroDTO libroDTO) {
        try {
            LibroDTO actualizado = libroService.actualizarLibro(id, libroDTO);
            return ResponseEntity.ok(Map.of(
                    "message", "Libro actualizado correctamente",
                    "libro", actualizado
            ));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        try {
            libroService.eliminarLibro(id);
            return ResponseEntity.ok(Map.of("message", "Libro eliminado correctamente"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarLibros(@RequestParam String titulo) {
        // Búsqueda por título
        List<LibroDTO> libros = libroService.buscarLibrosPorTitulo(titulo);
        if (libros.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "No se encontraron libros con ese título"
            ));
        }
        return ResponseEntity.ok(Map.of("libros", libros));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> obtenerLibrosDisponibles() {
        // Libros disponibles para préstamo
        List<LibroDTO> libros = libroService.obtenerLibrosDisponibles();
        return ResponseEntity.ok(Map.of("libros_disponibles", libros));
    }
}