package com.nopki.pagos.controller;

import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagosController {

    private static final Logger log = LoggerFactory.getLogger(PagosController.class);

    private final PagoService pagoService;

    // POST /api/pagos
    @PostMapping
    public ResponseEntity<PagoResponse> procesar(@Valid @RequestBody PagoRequest request) {
        log.info("POST /api/pagos - pedido: {}", request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarPago(request));
    }

    // GET /api/pagos
    @GetMapping
    public ResponseEntity<List<PagoResponse>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    // GET /api/pagos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    // GET /api/pagos/pedido/{pedidoId}
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponse> obtenerPorPedido(@PathVariable Long pedidoId) {
        log.info("GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.obtenerPorPedido(pedidoId));
    }

    // GET /api/pagos/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/pagos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    // GET /api/pagos/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoResponse>> listarPorEstado(@PathVariable EstadoPago estado) {
        log.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    // PATCH /api/pagos/{id}/reembolsar
    @PatchMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponse> reembolsar(@PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/reembolsar", id);
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}