package com.nopki.pagos.service;

import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.model.EstadoPago;
import java.util.List;

public interface PagoService {
    PagoResponse procesarPago(PagoRequest request);
    PagoResponse obtenerPorId(Long id);
    PagoResponse obtenerPorPedido(Long pedidoId);
    List<PagoResponse> listarTodos();
    List<PagoResponse> listarPorUsuario(Long usuarioId);
    List<PagoResponse> listarPorEstado(EstadoPago estado);
    PagoResponse reembolsar(Long id);
}