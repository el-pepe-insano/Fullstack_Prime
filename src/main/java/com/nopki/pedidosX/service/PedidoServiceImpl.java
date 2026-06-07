package com.nopki.pedidosX.service;

import com.nopki.pedidosX.client.AutenticacionClient;
import com.nopki.pedidosX.client.CatalogoClient;
import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.exception.PedidoNoEncontradoException;
import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.model.Pedido;
import com.nopki.pedidosX.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private static final Logger log = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private final PedidoRepository pedidoRepository;
    private final AutenticacionClient autenticacionClient;
    private final CatalogoClient catalogoClient;

    @Override
    public PedidoResponse crear(PedidoRequest request) {
        log.info("Creando pedido para usuario {} y juego {}", request.getUsuarioId(), request.getJuegoId());

        // Regla de negocio: validar que el usuario existe
        if (!autenticacionClient.existeUsuario(request.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario con id " + request.getUsuarioId() + " no existe");
        }

        // Regla de negocio: validar que el juego existe y está disponible
        if (!catalogoClient.existeJuego(request.getJuegoId())) {
            throw new IllegalArgumentException("El juego con id " + request.getJuegoId() + " no existe");
        }

        // Regla de negocio: no permitir comprar el mismo juego dos veces
        if (pedidoRepository.existsByUsuarioIdAndJuegoIdAndEstado(
                request.getUsuarioId(), request.getJuegoId(), EstadoPedido.ENTREGADO)) {
            throw new IllegalStateException("El usuario ya compró este juego");
        }

        // Obtener precio y título del catálogo vía WebClient
        BigDecimal precio = catalogoClient.obtenerPrecioJuego(request.getJuegoId());
        String titulo = catalogoClient.obtenerTituloJuego(request.getJuegoId());

        Pedido pedido = Pedido.builder()
                .usuarioId(request.getUsuarioId())
                .juegoId(request.getJuegoId())
                .tituloJuego(titulo)
                .total(precio)
                .build();

        Pedido guardado = pedidoRepository.save(pedido);
        log.info("Pedido {} creado en estado PENDIENTE", guardado.getId());
        return PedidoResponse.desde(guardado);
    }

    @Override
    public PedidoResponse obtenerPorId(Long id) {
        return PedidoResponse.desde(buscarPorId(id));
    }

    @Override
    public List<PedidoResponse> listarTodos() {
        log.info("Listando todos los pedidos");
        return pedidoRepository.findAll()
                .stream().map(PedidoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponse> listarPorUsuario(Long usuarioId) {
        log.info("Listando pedidos del usuario {}", usuarioId);
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream().map(PedidoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponse> historialUsuario(Long usuarioId) {
        log.info("Obteniendo historial del usuario {}", usuarioId);
        return pedidoRepository.historialUsuario(usuarioId)
                .stream().map(PedidoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponse> listarPorEstado(EstadoPedido estado) {
        log.info("Listando pedidos con estado {}", estado);
        return pedidoRepository.findByEstado(estado)
                .stream().map(PedidoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public PedidoResponse actualizarEstado(Long id, EstadoPedido estado) {
        log.info("Actualizando estado del pedido {} a {}", id, estado);
        Pedido pedido = buscarPorId(id);

        // Regla de negocio: no se puede cambiar estado de un pedido cancelado
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("No se puede modificar un pedido cancelado");
        }

        pedido.setEstado(estado);
        return PedidoResponse.desde(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse asignarLicencia(Long id, String codigoLicencia) {
        log.info("Asignando licencia {} al pedido {}", codigoLicencia, id);
        Pedido pedido = buscarPorId(id);
        pedido.setCodigoLicencia(codigoLicencia);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        return PedidoResponse.desde(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponse cancelar(Long id) {
        log.info("Cancelando pedido {}", id);
        Pedido pedido = buscarPorId(id);

        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido ya entregado");
        }
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalStateException("El pedido ya está cancelado");
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        return PedidoResponse.desde(pedidoRepository.save(pedido));
    }

    private Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNoEncontradoException("Pedido no encontrado con id: " + id));
    }
}