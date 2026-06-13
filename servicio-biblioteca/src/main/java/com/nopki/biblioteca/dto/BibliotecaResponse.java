package com.nopki.biblioteca.dto;

import com.nopki.biblioteca.model.EntradaBiblioteca;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaResponse {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private String codigoLicencia;
    private Long pedidoId;
    private LocalDateTime fechaAdquisicion;

    public static BibliotecaResponse desde(EntradaBiblioteca e) {
        return BibliotecaResponse.builder()
                .id(e.getId())
                .usuarioId(e.getUsuarioId())
                .juegoId(e.getJuegoId())
                .tituloJuego(e.getTituloJuego())
                .codigoLicencia(e.getCodigoLicencia())
                .pedidoId(e.getPedidoId())
                .fechaAdquisicion(e.getFechaAdquisicion())
                .build();
    }
}