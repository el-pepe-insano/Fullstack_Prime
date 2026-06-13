package nopki.resenas.dto;

import nopki.resenas.model.Resena;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponse {

    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private String nombreUsuario;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public static ResenaResponse desde(Resena r) {
        return ResenaResponse.builder()
                .id(r.getId())
                .usuarioId(r.getUsuarioId())
                .juegoId(r.getJuegoId())
                .tituloJuego(r.getTituloJuego())
                .nombreUsuario(r.getNombreUsuario())
                .calificacion(r.getCalificacion())
                .comentario(r.getComentario())
                .fechaCreacion(r.getFechaCreacion())
                .fechaActualizacion(r.getFechaActualizacion())
                .build();
    }
}