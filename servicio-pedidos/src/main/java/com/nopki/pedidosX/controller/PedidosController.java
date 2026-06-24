package com.nopki.pedidosX.controller;

import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestion de pedidos y compras de juegos")
public class PedidosController {

    private static final Logger log = LoggerFactory.getLogger(PedidosController.class);

    private final PedidoService pedidoService;

    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido de compra, validando que el usuario y el juego existan, y que el usuario no haya comprado el juego previamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente en estado PENDIENTE"),
            @ApiResponse(responseCode = "400", description = "El usuario o el juego no existen"),
            @ApiResponse(responseCode = "409", description = "El usuario ya compro este juego")
    })
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        log.info("POST /api/pedidos - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request));
    }

    @Operation(summary = "Listar todos los pedidos", description = "Retorna la lista completa de pedidos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        log.info("GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @Operation(summary = "Obtener pedido por id", description = "Busca un pedido especifico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@Parameter(description = "ID del pedido") @PathVariable Long id) {
        log.info("GET /api/pedidos/{}", id);
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @Operation(summary = "Listar pedidos de un usuario", description = "Retorna todos los pedidos asociados a un usuario especifico")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Historial de pedidos de un usuario", description = "Retorna los pedidos de un usuario ordenados por fecha descendente")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    @GetMapping("/usuario/{usuarioId}/historial")
    public ResponseEntity<List<PedidoResponse>> historialUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}/historial", usuarioId);
        return ResponseEntity.ok(pedidoService.historialUsuario(usuarioId));
    }

    @Operation(summary = "Listar pedidos por estado", description = "Filtra pedidos segun su estado (PENDIENTE, PAGADO, ENTREGADO, CANCELADO)")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> listarPorEstado(
            @Parameter(description = "Estado a filtrar") @PathVariable EstadoPedido estado) {
        log.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado de un pedido, siempre que no este cancelado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "409", description = "No se puede modificar un pedido cancelado")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Nuevo estado") @RequestParam EstadoPedido estado) {
        log.info("PATCH /api/pedidos/{}/estado - {}", id, estado);
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @Operation(summary = "Asignar licencia al pedido", description = "Endpoint interno usado por el servicio de pagos via WebClient. Asocia el codigo de licencia entregado y marca el pedido como ENTREGADO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Licencia asignada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PatchMapping("/{id}/licencia")
    public ResponseEntity<PedidoResponse> asignarLicencia(
            @Parameter(description = "ID del pedido") @PathVariable Long id,
            @Parameter(description = "Codigo de la licencia entregada") @RequestParam String codigoLicencia) {
        log.info("PATCH /api/pedidos/{}/licencia - {}", id, codigoLicencia);
        return ResponseEntity.ok(pedidoService.asignarLicencia(id, codigoLicencia));
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido, siempre que no este ya entregado o cancelado previamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido cancelado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
            @ApiResponse(responseCode = "409", description = "El pedido ya esta entregado o cancelado")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@Parameter(description = "ID del pedido") @PathVariable Long id) {
        log.info("PATCH /api/pedidos/{}/cancelar", id);
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }
}