package com.nopki.pedidosX.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoRequest {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El juego es obligatorio")
    private Long juegoId;
}