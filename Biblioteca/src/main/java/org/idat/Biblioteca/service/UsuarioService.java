package org.idat.Biblioteca.service;

import org.idat.Biblioteca.dto.UsuarioDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import java.util.List;

public interface UsuarioService {
    UsuarioDTO crearUsuario(UsuarioDTO usuarioDTO);
    UsuarioDTO validarUsuario(String email, String password);
    UsuarioDTO obtenerUsuarioPorId(Long id) throws ResourceNotFoundException;
    List<UsuarioDTO> listarUsuarios();
    UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) throws ResourceNotFoundException;
    void eliminarUsuario(Long id) throws ResourceNotFoundException;
    boolean esAdmin(Long usuarioId);
    UsuarioDTO obtenerUsuarioPorEmail(String email) throws ResourceNotFoundException;

    List<UsuarioDTO> buscarUsuariosPorNombre(String nombre);
}