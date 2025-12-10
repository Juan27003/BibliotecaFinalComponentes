package org.idat.Biblioteca.service;

import org.idat.Biblioteca.dto.LibroDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import java.util.List;

public interface LibroService {
    LibroDTO crearLibro(LibroDTO libroDTO);
    LibroDTO obtenerLibroPorId(Long id) throws ResourceNotFoundException;
    List<LibroDTO> listarLibros();
    LibroDTO actualizarLibro(Long id, LibroDTO libroDTO) throws ResourceNotFoundException;
    void eliminarLibro(Long id) throws ResourceNotFoundException;

    List<LibroDTO> buscarLibrosPorTitulo(String titulo);
    List<LibroDTO> obtenerLibrosDisponibles();
}