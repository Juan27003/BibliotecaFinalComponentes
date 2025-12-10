package org.idat.Biblioteca.service.impl;

import org.idat.Biblioteca.dto.PrestamoDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.model.Prestamo;
import org.idat.Biblioteca.repository.PrestamoRepository;
import org.idat.Biblioteca.service.PrestamoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Override
    public PrestamoDTO crearPrestamo(PrestamoDTO prestamoDTO) {
        // TU CÓDIGO ACTUAL AQUÍ
        return null; // Reemplaza con tu implementación
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorId(Long id) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));
        return convertirADTO(prestamo);
    }

    @Override
    public List<PrestamoDTO> listarPrestamos() {
        List<Prestamo> prestamos = prestamoRepository.findAll();
        return prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestamoDTO actualizarPrestamo(Long id, PrestamoDTO prestamoDTO, Long usuarioId) throws ResourceNotFoundException {
        // TU CÓDIGO ACTUAL AQUÍ
        return null; // Reemplaza con tu implementación
    }

    @Override
    public void eliminarPrestamo(Long id, Long usuarioId) throws ResourceNotFoundException {
        // TU CÓDIGO ACTUAL AQUÍ
    }

    @Override
    public List<PrestamoDTO> listarPrestamosPorUsuario(Long usuarioId) {
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(usuarioId);
        return prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorUsuarioId(Long usuarioId) throws ResourceNotFoundException {
        Prestamo prestamo = prestamoRepository.findTopByUsuarioIdOrderByFechaPrestamoDesc(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado"));
        return convertirADTO(prestamo);
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosActivos() {
        List<Prestamo> prestamos = prestamoRepository.findAllActivePrestamos();
        return prestamos.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private PrestamoDTO convertirADTO(Prestamo prestamo) {
        return PrestamoDTO.builder()
                .id(prestamo.getId())
                .usuarioId(prestamo.getUsuario().getId())
                .libroId(prestamo.getLibro().getId())
                .fechaPrestamo(prestamo.getFechaPrestamo())
                .fechaDevolucion(prestamo.getFechaDevolucion())
                .estado(prestamo.getEstado())
                .build();
    }
}