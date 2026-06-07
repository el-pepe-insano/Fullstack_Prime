package com.GodOfGames.Pedidos.services;

import com.GodOfGames.Pedidos.dtos.PedidoRequestDTO;
import com.GodOfGames.Pedidos.dtos.PedidoResponseDTO;
import com.GodOfGames.Pedidos.dtos.ProductoClientDTO;
import com.GodOfGames.Pedidos.exceptions.ResourceNotFoundException;
import com.GodOfGames.Pedidos.models.DetallePedido;
import com.GodOfGames.Pedidos.models.EstadoPedido;
import com.GodOfGames.Pedidos.models.Pedido;
import com.GodOfGames.Pedidos.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final WebClient webClient;

    @Value("${INVENTARIO_SERVICE_URL:http://inventario-service:8084}")
    private String inventarioServiceUrl;

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoDTO, String usuarioId, String token) {

        Pedido pedido = Pedido.builder()
                .usuarioId(usuarioId)
                .fechaCreacion(LocalDateTime.now())
                .estado(EstadoPedido.PENDIENTE)
                .build();

        BigDecimal totalPedido = BigDecimal.ZERO;

        for (PedidoRequestDTO.DetalleRequestDTO detalleDTO : pedidoDTO.getDetalles()) {

            // 1. Verificar que el producto existe y reservar stock
            try {
                ProductoClientDTO producto = webClient.post()
                        .uri(inventarioServiceUrl + "/api/productos/" + detalleDTO.getProductoId()
                                + "/reservar?cantidad=" + detalleDTO.getCantidad())
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .bodyToMono(ProductoClientDTO.class)
                        .block();

                log.info("Stock reservado para producto ID: {}, cantidad: {}",
                        detalleDTO.getProductoId(), detalleDTO.getCantidad());

            } catch (Exception e) {
                log.error("Error reservando stock para producto ID {}: {}",
                        detalleDTO.getProductoId(), e.getMessage());
                throw new RuntimeException("No se pudo reservar stock para el producto ID: "
                        + detalleDTO.getProductoId() + ". " + e.getMessage());
            }

            DetallePedido detalle = DetallePedido.builder()
                    .productoId(detalleDTO.getProductoId())
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(detalleDTO.getPrecioUnitario())
                    .build();

            pedido.addDetalle(detalle);

            BigDecimal subtotal = detalleDTO.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            totalPedido = totalPedido.add(subtotal);
        }

        pedido.setTotal(totalPedido);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        return mapearAPedidoResponseDTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedidoPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return mapearAPedidoResponseDTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::mapearAPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponseDTO actualizarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        pedido.setEstado(nuevoEstado);
        return mapearAPedidoResponseDTO(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerPedidosPorUsuario(String usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::mapearAPedidoResponseDTO)
                .collect(Collectors.toList());
    }

    private PedidoResponseDTO mapearAPedidoResponseDTO(Pedido pedido) {
        List<PedidoResponseDTO.DetalleResponseDTO> detallesDTO = pedido.getDetalles().stream()
                .map(d -> PedidoResponseDTO.DetalleResponseDTO.builder()
                        .id(d.getId())
                        .productoId(d.getProductoId())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuarioId())
                .fechaCreacion(pedido.getFechaCreacion())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .detalles(detallesDTO)
                .build();
    }
}