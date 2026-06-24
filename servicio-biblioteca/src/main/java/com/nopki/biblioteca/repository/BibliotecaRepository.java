package com.nopki.biblioteca.repository;

import com.nopki.biblioteca.model.EntradaBiblioteca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BibliotecaRepository extends JpaRepository<EntradaBiblioteca, Long> {

    List<EntradaBiblioteca> findByUsuarioId(Long usuarioId);

    Optional<EntradaBiblioteca> findByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    List<EntradaBiblioteca> findByJuegoId(Long juegoId);

    long countByUsuarioId(Long usuarioId);
}