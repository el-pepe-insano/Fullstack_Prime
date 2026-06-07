package com.nopki.pedidosX.controller;

import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.service.PedidoService;
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
public class PedidosController {

    private static final Logger log = LoggerFactory.getLogger(PedidosController.class);

    private final PedidoService pedidoService;

    // POST /api/pedidos
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request) {
        log.info("POST /api/pedidos - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request));
    }

    // GET /api/pedidos
    @GetMapping
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        log.info("GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    // GET /api/pedidos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/pedidos/{}", id);
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    // GET /api/pedidos/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    // GET /api/pedidos/usuario/{usuarioId}/historial
    @GetMapping("/usuario/{usuarioId}/historial")
    public ResponseEntity<List<PedidoResponse>> historialUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/pedidos/usuario/{}/historial", usuarioId);
        return ResponseEntity.ok(pedidoService.historialUsuario(usuarioId));
    }

    // GET /api/pedidos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoResponse>> listarPorEstado(@PathVariable EstadoPedido estado) {
        log.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(pedidoService.listarPorEstado(estado));
    }

    // PATCH /api/pedidos/{id}/estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido estado) {
        log.info("PATCH /api/pedidos/{}/estado - {}", id, estado);
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    // PATCH /api/pedidos/{id}/licencia — usado por servicio de pagos
    @PatchMapping("/{id}/licencia")
    public ResponseEntity<PedidoResponse> asignarLicencia(
            @PathVariable Long id,
            @RequestParam String codigoLicencia) {
        log.info("PATCH /api/pedidos/{}/licencia - {}", id, codigoLicencia);
        return ResponseEntity.ok(pedidoService.asignarLicencia(id, codigoLicencia));
    }

    // PATCH /api/pedidos/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id) {
        log.info("PATCH /api/pedidos/{}/cancelar", id);
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }
}