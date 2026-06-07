package com.nopki.licencias.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class LicenciaRequest {

    @NotNull(message = "El id del juego es obligatorio")
    private Long juegoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "Debe generarse al menos 1 licencia")
    private Integer cantidad;
}