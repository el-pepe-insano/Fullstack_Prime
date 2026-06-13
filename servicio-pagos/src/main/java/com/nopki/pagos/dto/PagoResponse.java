package com.nopki.pagos.dto;

import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.model.Pago;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponse {

    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal monto;
    private String metodoPago;
    private EstadoPago estado;
    private String codigoLicencia;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaActualizacion;

    public static PagoResponse desde(Pago p) {
        return PagoResponse.builder()
                .id(p.getId())
                .pedidoId(p.getPedidoId())
                .usuarioId(p.getUsuarioId())
                .monto(p.getMonto())
                .metodoPago(p.getMetodoPago())
                .estado(p.getEstado())
                .codigoLicencia(p.getCodigoLicencia())
                .fechaPago(p.getFechaPago())
                .fechaActualizacion(p.getFechaActualizacion())
                .build();
    }
}