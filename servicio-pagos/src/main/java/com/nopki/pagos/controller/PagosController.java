package com.nopki.pagos.controller;

import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.service.PagoService;
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
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Procesamiento de pagos y entrega de licencias")
public class PagosController {

    private static final Logger log = LoggerFactory.getLogger(PagosController.class);

    private final PagoService pagoService;

    @Operation(summary = "Procesar pago", description = "Procesa el pago de un pedido. Valida el pedido, aprueba el pago, solicita una licencia al servicio de licencias y notifica al servicio de pedidos con el codigo entregado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado y licencia entregada exitosamente"),
            @ApiResponse(responseCode = "400", description = "El pedido no existe"),
            @ApiResponse(responseCode = "409", description = "El pedido ya tiene un pago registrado o no esta en estado PENDIENTE")
    })
    @PostMapping
    public ResponseEntity<PagoResponse> procesar(@Valid @RequestBody PagoRequest request) {
        log.info("POST /api/pagos - pedido: {}", request.getPedidoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesarPago(request));
    }

    @Operation(summary = "Listar todos los pagos", description = "Retorna la lista completa de pagos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PagoResponse>> listarTodos() {
        log.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @Operation(summary = "Obtener pago por id", description = "Busca un pago especifico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> obtenerPorId(@Parameter(description = "ID del pago") @PathVariable Long id) {
        log.info("GET /api/pagos/{}", id);
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener pago por pedido", description = "Busca el pago asociado a un pedido especifico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe pago para ese pedido")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponse> obtenerPorPedido(@Parameter(description = "ID del pedido") @PathVariable Long pedidoId) {
        log.info("GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.obtenerPorPedido(pedidoId));
    }

    @Operation(summary = "Listar pagos de un usuario", description = "Retorna todos los pagos realizados por un usuario especifico")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponse>> listarPorUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/pagos/usuario/{}", usuarioId);
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Listar pagos por estado", description = "Filtra pagos segun su estado (PENDIENTE, APROBADO, RECHAZADO, REEMBOLSADO)")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PagoResponse>> listarPorEstado(
            @Parameter(description = "Estado a filtrar") @PathVariable EstadoPago estado) {
        log.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.listarPorEstado(estado));
    }

    @Operation(summary = "Reembolsar pago", description = "Reembolsa un pago aprobado y notifica al servicio de pedidos para cancelar el pedido asociado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago reembolsado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "409", description = "Solo se pueden reembolsar pagos aprobados")
    })
    @PatchMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponse> reembolsar(@Parameter(description = "ID del pago") @PathVariable Long id) {
        log.info("PATCH /api/pagos/{}/reembolsar", id);
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}