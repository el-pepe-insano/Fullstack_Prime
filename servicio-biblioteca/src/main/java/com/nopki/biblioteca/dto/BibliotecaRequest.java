package com.nopki.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BibliotecaRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El juego es obligatorio")
    private Long juegoId;

    @NotBlank(message = "El código de licencia es obligatorio")
    private String codigoLicencia;

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;
}