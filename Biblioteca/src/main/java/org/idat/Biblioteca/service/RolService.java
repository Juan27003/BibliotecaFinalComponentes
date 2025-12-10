package org.idat.Biblioteca.service;

import org.idat.Biblioteca.dto.RolDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;

import java.util.List;

public interface RolService {
    RolDTO crearRol(RolDTO rolDTO);
    RolDTO obtenerRolPorId(Long id) throws ResourceNotFoundException;
    List<RolDTO> listarRoles();
    RolDTO actualizarRol(Long id, RolDTO rolDTO) throws ResourceNotFoundException;
    void eliminarRol(Long id) throws ResourceNotFoundException;
}
