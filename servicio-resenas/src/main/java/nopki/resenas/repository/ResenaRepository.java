package nopki.resenas.repository;

import nopki.resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByJuegoId(Long juegoId);

    List<Resena> findByUsuarioId(Long usuarioId);

    Optional<Resena> findByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.juegoId = :juegoId")
    Double promedioCalificacionPorJuego(@Param("juegoId") Long juegoId);

    @Query("SELECT r FROM Resena r WHERE r.juegoId = :juegoId ORDER BY r.calificacion DESC")
    List<Resena> findByJuegoIdOrderByCalificacion(@Param("juegoId") Long juegoId);

    @Query("SELECT r FROM Resena r WHERE r.calificacion = :calificacion")
    List<Resena> findByCalificacion(@Param("calificacion") Integer calificacion);

    long countByJuegoId(Long juegoId);
}