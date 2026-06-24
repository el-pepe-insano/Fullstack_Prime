package com.nopki.pagos.repository;

import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoId(Long pedidoId);

    List<Pago> findByUsuarioId(Long usuarioId);

    List<Pago> findByEstado(EstadoPago estado);

    List<Pago> findByUsuarioIdAndEstado(Long usuarioId, EstadoPago estado);

    boolean existsByPedidoId(Long pedidoId);
}