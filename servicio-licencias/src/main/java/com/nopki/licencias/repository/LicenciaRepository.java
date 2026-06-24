package com.nopki.licencias.repository;

import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.model.Licencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenciaRepository extends JpaRepository<Licencia, Long> {
    List<Licencia> findByJuegoId(Long juegoId);
    List<Licencia> findByEstado(EstadoLicencia estado);
    List<Licencia> findByJuegoIdAndEstado(Long juegoId, EstadoLicencia estado);
    Optional<Licencia> findFirstByJuegoIdAndEstado(Long juegoId, EstadoLicencia estado);
    Optional<Licencia> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    long countByJuegoIdAndEstado(Long juegoId, EstadoLicencia estado);
}