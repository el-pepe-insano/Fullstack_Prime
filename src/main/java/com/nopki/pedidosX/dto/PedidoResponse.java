package com.nopki.pedidosX.dto;

import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.model.Pedido;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponse {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private BigDecimal total;
    private EstadoPedido estado;
    private String codigoLicencia;
    private LocalDateTime fechaPedido;
    private LocalDateTime fechaActualizacion;

    public static PedidoResponse desde(Pedido p) {
        return PedidoResponse.builder()
                .id(p.getId())
                .usuarioId(p.getUsuarioId())
                .juegoId(p.getJuegoId())
                .tituloJuego(p.getTituloJuego())
                .total(p.getTotal())
                .estado(p.getEstado())
                .codigoLicencia(p.getCodigoLicencia())
                .fechaPedido(p.getFechaPedido())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}