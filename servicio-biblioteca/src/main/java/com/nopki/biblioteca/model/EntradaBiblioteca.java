package com.nopki.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "biblioteca",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "juego_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntradaBiblioteca {

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

    @Column(name = "codigo_licencia", nullable = false, length = 50)
    private String codigoLicencia;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "fecha_adquisicion", nullable = false, updatable = false)
    private LocalDateTime fechaAdquisicion;

    @PrePersist
    protected void onCreate() {
        this.fechaAdquisicion = LocalDateTime.now();
    }
}