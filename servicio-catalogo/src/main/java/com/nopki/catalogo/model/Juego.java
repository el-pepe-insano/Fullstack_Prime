package com.nopki.catalogo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "juegos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 1, max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotBlank(message = "El desarrollador no puede estar vacío")
    @Column(nullable = false, length = 100)
    private String desarrollador;

    @NotBlank(message = "La plataforma no puede estar vacía")
    @Column(nullable = false, length = 50)
    private String plataforma;

    @NotNull(message = "El género es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column(length = 500)
    private String imagenUrl;

    @Column(nullable = false)
    private boolean disponible;

    @Column(name = "fecha_lanzamiento")
    private String fechaLanzamiento;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.disponible = true;
    }
}