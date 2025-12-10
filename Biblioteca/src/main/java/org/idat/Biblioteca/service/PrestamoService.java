package org.idat.Biblioteca.service;

import org.idat.Biblioteca.dto.PrestamoDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import java.util.List;

public interface PrestamoService {
    PrestamoDTO crearPrestamo(PrestamoDTO prestamoDTO);
    PrestamoDTO obtenerPrestamoPorId(Long id) throws ResourceNotFoundException;
    List<PrestamoDTO> listarPrestamos();
    PrestamoDTO actualizarPrestamo(Long id, PrestamoDTO prestamoDTO, Long usuarioId) throws ResourceNotFoundException;
    void eliminarPrestamo(Long id, Long usuarioId) throws ResourceNotFoundException;
    List<PrestamoDTO> listarPrestamosPorUsuario(Long usuarioId);
    PrestamoDTO obtenerPrestamoPorUsuarioId(Long usuarioId) throws ResourceNotFoundException;
    
    List<PrestamoDTO> obtenerPrestamosActivos();
}