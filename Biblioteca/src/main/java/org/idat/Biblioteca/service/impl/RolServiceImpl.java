package org.idat.Biblioteca.service.impl;

import org.idat.Biblioteca.dto.RolDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.model.Rol;
import org.idat.Biblioteca.repository.RolRepository;
import org.idat.Biblioteca.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public RolDTO crearRol(RolDTO rolDTO) {
        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre());
        rolRepository.save(rol);

        rolDTO.setId(rol.getId());
        return rolDTO;
    }

    @Override
    public RolDTO obtenerRolPorId(Long id) throws ResourceNotFoundException {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        return new RolDTO(rol.getId(), rol.getNombre());
    }

    @Override
    public List<RolDTO> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> new RolDTO(rol.getId(), rol.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public RolDTO actualizarRol(Long id, RolDTO rolDTO) throws ResourceNotFoundException {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        rol.setNombre(rolDTO.getNombre());
        rolRepository.save(rol);
        rolDTO.setId(rol.getId());
        return rolDTO;
    }

    @Override
    public void eliminarRol(Long id) throws ResourceNotFoundException {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id: " + id));
        rolRepository.delete(rol);
    }
}
