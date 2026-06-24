package com.nopki.catalogo.dto;

import com.nopki.catalogo.model.Genero;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JuegoRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 150)
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotBlank(message = "El desarrollador es obligatorio")
    private String desarrollador;

    @NotBlank(message = "La plataforma es obligatoria")
    private String plataforma;

    @NotNull(message = "El género es obligatorio")
    private Genero genero;

    private String imagenUrl;

    private String fechaLanzamiento;
}