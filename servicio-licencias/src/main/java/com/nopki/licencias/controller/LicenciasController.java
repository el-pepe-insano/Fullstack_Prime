package com.nopki.licencias.controller;

import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import com.nopki.licencias.service.LicenciaService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/licencias")
@RequiredArgsConstructor
@Tag(name = "Licencias", description = "Generacion y gestion de licencias (keys) de juegos")
public class LicenciasController {

    private static final Logger log = LoggerFactory.getLogger(LicenciasController.class);

    private final LicenciaService licenciaService;

    @Operation(summary = "Generar licencias", description = "Genera una cantidad especifica de licencias unicas para un juego, validando previamente que el juego exista en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Licencias generadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "El juego no existe en el catalogo")
    })
    @PostMapping("/generar")
    public ResponseEntity<List<LicenciaResponse>> generar(@Valid @RequestBody LicenciaRequest request) {
        log.info("POST /api/licencias/generar - juegoId: {}", request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(licenciaService.generarLicencias(request));
    }

    @Operation(summary = "Obtener licencia por id", description = "Busca una licencia especifica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Licencia encontrada"),
            @ApiResponse(responseCode = "404", description = "Licencia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LicenciaResponse> obtenerPorId(
            @Parameter(description = "ID de la licencia") @PathVariable Long id) {
        log.info("GET /api/licencias/{}", id);
        return ResponseEntity.ok(licenciaService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener licencia por codigo", description = "Busca una licencia especifica por su codigo unico (key)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Licencia encontrada"),
            @ApiResponse(responseCode = "404", description = "Licencia no encontrada")
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<LicenciaResponse> obtenerPorCodigo(
            @Parameter(description = "Codigo de la licencia") @PathVariable String codigo) {
        log.info("GET /api/licencias/codigo/{}", codigo);
        return ResponseEntity.ok(licenciaService.obtenerPorCodigo(codigo));
    }

    @Operation(summary = "Listar licencias por juego", description = "Retorna todas las licencias asociadas a un juego, sin importar su estado")
    @ApiResponse(responseCode = "200", description = "Lista de licencias obtenida exitosamente")
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<LicenciaResponse>> listarPorJuego(
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}", juegoId);
        return ResponseEntity.ok(licenciaService.listarPorJuego(juegoId));
    }

    @Operation(summary = "Listar licencias disponibles por juego", description = "Retorna solo las licencias con estado DISPONIBLE de un juego")
    @ApiResponse(responseCode = "200", description = "Lista de licencias disponibles obtenida exitosamente")
    @GetMapping("/juego/{juegoId}/disponibles")
    public ResponseEntity<List<LicenciaResponse>> listarDisponibles(
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}/disponibles", juegoId);
        return ResponseEntity.ok(licenciaService.listarDisponiblesPorJuego(juegoId));
    }

    @Operation(summary = "Consultar stock de licencias", description = "Retorna la cantidad de licencias disponibles para un juego")
    @ApiResponse(responseCode = "200", description = "Stock obtenido exitosamente")
    @GetMapping("/juego/{juegoId}/stock")
    public ResponseEntity<Map<String, Object>> contarStock(
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}/stock", juegoId);
        return ResponseEntity.ok(Map.of(
                "juegoId", juegoId,
                "disponibles", licenciaService.contarDisponiblesPorJuego(juegoId)
        ));
    }

    @Operation(summary = "Asignar licencia a un pedido", description = "Endpoint interno usado por el servicio de pagos via WebClient. Asigna la primera licencia disponible de un juego a un pedido especifico y la marca como VENDIDA")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Licencia asignada exitosamente"),
            @ApiResponse(responseCode = "409", description = "No hay licencias disponibles para el juego")
    })
    @PostMapping("/asignar")
    public ResponseEntity<LicenciaResponse> asignar(
            @Parameter(description = "ID del juego") @RequestParam Long juegoId,
            @Parameter(description = "ID del pedido") @RequestParam Long pedidoId) {
        log.info("POST /api/licencias/asignar - juegoId: {}, pedidoId: {}", juegoId, pedidoId);
        return ResponseEntity.ok(licenciaService.asignarLicencia(juegoId, pedidoId));
    }

    @Operation(summary = "Revocar licencia", description = "Cambia el estado de una licencia a REVOCADA, invalidandola permanentemente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Licencia revocada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Licencia no encontrada"),
            @ApiResponse(responseCode = "409", description = "La licencia ya estaba revocada")
    })
    @PatchMapping("/{id}/revocar")
    public ResponseEntity<LicenciaResponse> revocar(@Parameter(description = "ID de la licencia") @PathVariable Long id) {
        log.info("PATCH /api/licencias/{}/revocar", id);
        return ResponseEntity.ok(licenciaService.revocarLicencia(id));
    }

    @Operation(summary = "Validar codigo de licencia", description = "Verifica que un codigo de licencia exista y este en estado VENDIDA")
    @ApiResponse(responseCode = "200", description = "Resultado de la validacion")
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(
            @Parameter(description = "Codigo de la licencia a validar") @RequestParam String codigo) {
        log.info("GET /api/licencias/validar?codigo={}", codigo);
        return ResponseEntity.ok(Map.of("valido", licenciaService.validarCodigo(codigo)));
    }
}