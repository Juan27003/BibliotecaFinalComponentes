package org.idat.Biblioteca.service.impl;

import org.idat.Biblioteca.dto.LibroDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.model.Libro;
import org.idat.Biblioteca.repository.LibroRepository;
import org.idat.Biblioteca.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public LibroDTO crearLibro(LibroDTO libroDTO) {
        Libro libro = convertirAEntidad(libroDTO);
        Libro libroGuardado = libroRepository.save(libro);
        return convertirADTO(libroGuardado);
    }

    @Override
    public LibroDTO obtenerLibroPorId(Long id) throws ResourceNotFoundException {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        return convertirADTO(libro);
    }

    @Override
    public List<LibroDTO> listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        return libros.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public LibroDTO actualizarLibro(Long id, LibroDTO libroDTO) throws ResourceNotFoundException {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));

        libro.setTitulo(libroDTO.getTitulo());
        libro.setAutor(libroDTO.getAutor());
        libro.setCantidadDisponible(libroDTO.getCantidadDisponible());

        Libro libroActualizado = libroRepository.save(libro);
        return convertirADTO(libroActualizado);
    }

    @Override
    public void eliminarLibro(Long id) throws ResourceNotFoundException {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado");
        }
        libroRepository.deleteById(id);
    }

    // NUEVOS MÉTODOS
    @Override
    public List<LibroDTO> buscarLibrosPorTitulo(String titulo) {
        List<Libro> libros = libroRepository.buscarPorTitulo(titulo);
        return libros.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LibroDTO> obtenerLibrosDisponibles() {
        List<Libro> libros = libroRepository.findLibrosDisponibles();
        return libros.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // MÉTODOS DE CONVERSIÓN
    private LibroDTO convertirADTO(Libro libro) {
        return LibroDTO.builder()
                .id(libro.getId())
                .titulo(libro.getTitulo())
                .autor(libro.getAutor())
                .cantidadDisponible(libro.getCantidadDisponible())
                .build();
    }

    private Libro convertirAEntidad(LibroDTO libroDTO) {
        return Libro.builder()
                .id(libroDTO.getId())
                .titulo(libroDTO.getTitulo())
                .autor(libroDTO.getAutor())
                .cantidadDisponible(libroDTO.getCantidadDisponible())
                .build();
    }
}