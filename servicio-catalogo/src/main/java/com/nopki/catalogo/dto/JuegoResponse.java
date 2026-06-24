package com.nopki.catalogo.dto;

import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.model.Juego;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JuegoResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private String desarrollador;
    private String plataforma;
    private Genero genero;
    private String imagenUrl;
    private boolean disponible;
    private String fechaLanzamiento;
    private LocalDateTime fechaRegistro;

    public static JuegoResponse desde(Juego j) {
        return JuegoResponse.builder()
                .id(j.getId())
                .titulo(j.getTitulo())
                .descripcion(j.getDescripcion())
                .precio(j.getPrecio())
                .desarrollador(j.getDesarrollador())
                .plataforma(j.getPlataforma())
                .genero(j.getGenero())
                .imagenUrl(j.getImagenUrl())
                .disponible(j.isDisponible())
                .fechaLanzamiento(j.getFechaLanzamiento())
                .fechaRegistro(j.getFechaRegistro())
                .build();
    }
}