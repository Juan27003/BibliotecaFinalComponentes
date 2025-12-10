package org.idat.Biblioteca.service.impl;

import org.idat.Biblioteca.dto.PrestamoDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.model.Prestamo;
import org.idat.Biblioteca.model.Usuario;
import org.idat.Biblioteca.model.Libro;
import org.idat.Biblioteca.repository.PrestamoRepository;
import org.idat.Biblioteca.repository.UsuarioRepository;
import org.idat.Biblioteca.repository.LibroRepository;
import org.idat.Biblioteca.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public PrestamoDTO crearPrestamo(PrestamoDTO dto) throws ResourceNotFoundException {
        // Obtener libro
        Libro libro = libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        if (libro.getCantidadDisponible() <= 0) {
            throw new RuntimeException("Libro no disponible para préstamo");
        }

        // Reducir stock
        libro.setCantidadDisponible(libro.getCantidadDisponible() - 1);
        libroRepository.save(libro);

        // Crear préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado")));
        prestamo.setFechaPrestamo(new Date());
        prestamo.setEstado("ACTIVO");

        Prestamo guardado = prestamoRepository.save(prestamo);
        return mapToDTO(guardado);
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorId(Long id) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));
        return mapToDTO(prestamo);
    }

    @Override
    public List<PrestamoDTO> listarPrestamos() {
        return prestamoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestamoDTO actualizarPrestamo(Long id, PrestamoDTO dto, Long usuarioId) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));

        if (dto.getFechaDevolucion() != null && "ACTIVO".equals(prestamo.getEstado())) {
            // Devolver libro: sumar stock
            Libro libro = prestamo.getLibro();
            libro.setCantidadDisponible(libro.getCantidadDisponible() + 1);
            libroRepository.save(libro);

            prestamo.setFechaDevolucion(dto.getFechaDevolucion());
            prestamo.setEstado("DEVUELTO");
        }

        return mapToDTO(prestamoRepository.save(prestamo));
    }

    @Override
    public void eliminarPrestamo(Long id, Long usuarioId) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + id));
        prestamoRepository.delete(prestamo);
    }

    @Override
    public List<PrestamoDTO> listarPrestamosPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorUsuarioId(Long usuarioId) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findByUsuarioId(usuarioId)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado para el usuario con id: " + usuarioId));
        return mapToDTO(prestamo);
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = prestamoRepository.findAllActivePrestamos();
        return prestamos.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PrestamoDTO mapToDTO(Prestamo prestamo) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(prestamo.getId());
        dto.setUsuarioId(prestamo.getUsuario().getId());
        dto.setUsuarioNombre(prestamo.getUsuario().getNombre());
        dto.setLibroId(prestamo.getLibro().getId());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        dto.setEstado(prestamo.getEstado());
        return dto;
    }
}