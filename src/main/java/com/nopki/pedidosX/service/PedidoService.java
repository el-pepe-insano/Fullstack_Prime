package com.nopki.pedidosX.service;

import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.model.EstadoPedido;
import java.util.List;

public interface PedidoService {
    PedidoResponse crear(PedidoRequest request);
    PedidoResponse obtenerPorId(Long id);
    List<PedidoResponse> listarTodos();
    List<PedidoResponse> listarPorUsuario(Long usuarioId);
    List<PedidoResponse> historialUsuario(Long usuarioId);
    List<PedidoResponse> listarPorEstado(EstadoPedido estado);
    PedidoResponse actualizarEstado(Long id, EstadoPedido estado);
    PedidoResponse asignarLicencia(Long id, String codigoLicencia);
    PedidoResponse cancelar(Long id);
}