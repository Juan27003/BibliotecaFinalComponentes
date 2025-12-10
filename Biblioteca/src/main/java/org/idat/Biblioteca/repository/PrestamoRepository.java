package org.idat.Biblioteca.repository;

import org.idat.Biblioteca.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByUsuarioId(Long usuarioId);
    Optional<Prestamo> findTopByUsuarioIdOrderByFechaPrestamoDesc(Long usuarioId);

    // NUEVA QUERY
    @Query("SELECT p FROM Prestamo p WHERE p.estado = 'ACTIVO'")
    List<Prestamo> findAllActivePrestamos();
}