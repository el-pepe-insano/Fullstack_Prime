package nopki.resenas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "juego_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull
    @Column(name = "juego_id", nullable = false)
    private Long juegoId;

    @Column(name = "titulo_juego", nullable = false, length = 150)
    private String tituloJuego;

    @Column(name = "nombre_usuario", nullable = false, length = 100)
    private String nombreUsuario;

    @NotNull
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Column(nullable = false)
    private Integer calificacion;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}