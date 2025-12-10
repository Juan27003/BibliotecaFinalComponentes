package org.idat.Biblioteca.service.impl;

import org.idat.Biblioteca.dto.UsuarioDTO;
import org.idat.Biblioteca.exception.ResourceNotFoundException;
import org.idat.Biblioteca.model.Rol;
import org.idat.Biblioteca.model.Usuario;
import org.idat.Biblioteca.repository.RolRepository;
import org.idat.Biblioteca.repository.UsuarioRepository;
import org.idat.Biblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {

        Rol rolUsuario = rolRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword());
        usuario.setRol(rolUsuario);

        Usuario guardado = usuarioRepository.save(usuario);

        UsuarioDTO response = new UsuarioDTO();
        response.setId(guardado.getId());
        response.setNombre(guardado.getNombre());
        response.setEmail(guardado.getEmail());
        response.setRolNombre(guardado.getRol().getNombre());

        return response;
    }

    @Override
    public UsuarioDTO validarUsuario(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());

        return dto;
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long id) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        return dto;
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(u.getId());
                    dto.setNombre(u.getNombre());
                    dto.setEmail(u.getEmail());
                    dto.setRolId(u.getRol().getId());
                    dto.setRolNombre(u.getRol().getNombre());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO usuarioDTO) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setEmail(usuarioDTO.getEmail());

        usuarioRepository.save(usuario);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRolId(usuario.getRol().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        return dto;
    }

    @Override
    public void eliminarUsuario(Long id) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuarioRepository.delete(usuario);
    }

    @Override
    public boolean esAdmin(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return usuario.getRol().getNombre().equalsIgnoreCase("ADMIN");
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorEmail(String email) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rolId(usuario.getRol().getId())
                .rolNombre(usuario.getRol().getNombre())
                .build();
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorNombre(String nombre) {
        List<Usuario> usuarios = usuarioRepository.buscarPorNombre(nombre);
        return usuarios.stream()
                .map(u -> {
                    UsuarioDTO dto = new UsuarioDTO();
                    dto.setId(u.getId());
                    dto.setNombre(u.getNombre());
                    dto.setEmail(u.getEmail());
                    dto.setRolId(u.getRol().getId());
                    dto.setRolNombre(u.getRol().getNombre());
                    return dto;
                }).collect(Collectors.toList());
    }
}