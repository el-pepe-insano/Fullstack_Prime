package nopki.resenas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El juego es obligatorio")
    private Long juegoId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    private String comentario;
}