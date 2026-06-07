package com.nopki.licencias.dto;

import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.model.Licencia;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicenciaResponse {

    private Long id;
    private Long juegoId;
    private String codigo;
    private EstadoLicencia estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaVenta;
    private Long pedidoId;

    public static LicenciaResponse desde(Licencia l) {
        return LicenciaResponse.builder()
                .id(l.getId())
                .juegoId(l.getJuegoId())
                .codigo(l.getCodigo())
                .estado(l.getEstado())
                .fechaCreacion(l.getFechaCreacion())
                .fechaVenta(l.getFechaVenta())
                .pedidoId(l.getPedidoId())
                .build();
    }
}