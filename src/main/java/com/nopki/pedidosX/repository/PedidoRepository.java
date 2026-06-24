package com.nopki.pedidosX.repository;

import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);
    List<Pedido> findByJuegoId(Long juegoId);
    @Query("SELECT p FROM Pedido p WHERE p.usuarioId = :usuarioId ORDER BY p.fechaPedido DESC")
    List<Pedido> historialUsuario(@Param("usuarioId") Long usuarioId);
    boolean existsByUsuarioIdAndJuegoIdAndEstado(Long usuarioId, Long juegoId, EstadoPedido estado);
}