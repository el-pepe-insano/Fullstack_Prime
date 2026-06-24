package com.nopki.pagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequest {

    @NotNull(message = "El pedido es obligatorio")
    private Long pedidoId;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El monto es obligatorio")
    private java.math.BigDecimal monto;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;
}