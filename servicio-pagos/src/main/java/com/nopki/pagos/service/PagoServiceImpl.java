package com.nopki.pagos.service;

import com.nopki.pagos.client.LicenciaClient;
import com.nopki.pagos.client.PedidoClient;
import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.exception.PagoNoEncontradoException;
import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.model.Pago;
import com.nopki.pagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoRepository pagoRepository;
    private final PedidoClient pedidoClient;
    private final LicenciaClient licenciaClient;

    @Override
    public PagoResponse procesarPago(PagoRequest request) {
        log.info("Procesando pago para pedido {}", request.getPedidoId());

        // Regla de negocio: no procesar un pedido ya pagado
        if (pagoRepository.existsByPedidoId(request.getPedidoId())) {
            throw new IllegalStateException("El pedido ya tiene un pago registrado");
        }

        // Validar que el pedido existe vía WebClient
        Map pedido = pedidoClient.obtenerPedido(request.getPedidoId());
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido con id " + request.getPedidoId() + " no existe");
        }

        // Regla de negocio: validar que el pedido está en estado PENDIENTE
        String estadoPedido = pedido.get("estado").toString();
        if (!"PENDIENTE".equals(estadoPedido)) {
            throw new IllegalStateException("El pedido no está en estado PENDIENTE");
        }

        // Guardar pago en estado APROBADO (simulación de pasarela)
        Pago pago = Pago.builder()
                .pedidoId(request.getPedidoId())
                .usuarioId(request.getUsuarioId())
                .monto(request.getMonto())
                .metodoPago(request.getMetodoPago())
                .build();

        pago.setEstado(EstadoPago.APROBADO);
        Pago guardado = pagoRepository.save(pago);
        log.info("Pago {} aprobado para pedido {}", guardado.getId(), request.getPedidoId());

        // Obtener juegoId del pedido y asignar licencia vía WebClient
        Long juegoId = Long.valueOf(pedido.get("juegoId").toString());
        Map licencia = licenciaClient.asignarLicencia(juegoId, request.getPedidoId());

        if (licencia != null && licencia.get("codigo") != null) {
            String codigo = licencia.get("codigo").toString();
            guardado.setCodigoLicencia(codigo);
            guardado = pagoRepository.save(guardado);

            // Notificar al servicio de pedidos que la licencia fue entregada
            pedidoClient.asignarLicenciaAPedido(request.getPedidoId(), codigo);
            log.info("Licencia {} entregada al pedido {}", codigo, request.getPedidoId());
        } else {
            // Si no hay licencias disponibles actualizar pedido a PAGADO sin licencia aún
            pedidoClient.actualizarEstadoPedido(request.getPedidoId(), "PAGADO");
            log.warn("No se pudo asignar licencia al pedido {}", request.getPedidoId());
        }

        return PagoResponse.desde(guardado);
    }

    @Override
    public PagoResponse obtenerPorId(Long id) {
        return PagoResponse.desde(buscarPorId(id));
    }

    @Override
    public PagoResponse obtenerPorPedido(Long pedidoId) {
        log.info("Buscando pago del pedido {}", pedidoId);
        return PagoResponse.desde(
                pagoRepository.findByPedidoId(pedidoId)
                        .orElseThrow(() -> new PagoNoEncontradoException(
                                "Pago no encontrado para pedido id: " + pedidoId))
        );
    }

    @Override
    public List<PagoResponse> listarTodos() {
        log.info("Listando todos los pagos");
        return pagoRepository.findAll()
                .stream().map(PagoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<PagoResponse> listarPorUsuario(Long usuarioId) {
        log.info("Listando pagos del usuario {}", usuarioId);
        return pagoRepository.findByUsuarioId(usuarioId)
                .stream().map(PagoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<PagoResponse> listarPorEstado(EstadoPago estado) {
        log.info("Listando pagos con estado {}", estado);
        return pagoRepository.findByEstado(estado)
                .stream().map(PagoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public PagoResponse reembolsar(Long id) {
        log.info("Reembolsando pago {}", id);
        Pago pago = buscarPorId(id);

        // Regla de negocio: solo se pueden reembolsar pagos aprobados
        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new IllegalStateException("Solo se pueden reembolsar pagos aprobados");
        }

        pago.setEstado(EstadoPago.REEMBOLSADO);

        // Notificar al servicio de pedidos que fue cancelado
        pedidoClient.actualizarEstadoPedido(pago.getPedidoId(), "CANCELADO");

        return PagoResponse.desde(pagoRepository.save(pago));
    }

    private Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNoEncontradoException("Pago no encontrado con id: " + id));
    }
}