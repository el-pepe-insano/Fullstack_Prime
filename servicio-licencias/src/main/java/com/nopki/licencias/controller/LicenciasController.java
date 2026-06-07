package com.nopki.licencias.controller;

import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import com.nopki.licencias.service.LicenciaService;
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
public class LicenciasController {

    private static final Logger log = LoggerFactory.getLogger(LicenciasController.class);

    private final LicenciaService licenciaService;

    // POST /api/licencias/generar
    @PostMapping("/generar")
    public ResponseEntity<List<LicenciaResponse>> generar(@Valid @RequestBody LicenciaRequest request) {
        log.info("POST /api/licencias/generar - juegoId: {}", request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(licenciaService.generarLicencias(request));
    }

    // GET /api/licencias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<LicenciaResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/licencias/{}", id);
        return ResponseEntity.ok(licenciaService.obtenerPorId(id));
    }

    // GET /api/licencias/codigo/{codigo}
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<LicenciaResponse> obtenerPorCodigo(@PathVariable String codigo) {
        log.info("GET /api/licencias/codigo/{}", codigo);
        return ResponseEntity.ok(licenciaService.obtenerPorCodigo(codigo));
    }

    // GET /api/licencias/juego/{juegoId}
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<LicenciaResponse>> listarPorJuego(@PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}", juegoId);
        return ResponseEntity.ok(licenciaService.listarPorJuego(juegoId));
    }

    // GET /api/licencias/juego/{juegoId}/disponibles
    @GetMapping("/juego/{juegoId}/disponibles")
    public ResponseEntity<List<LicenciaResponse>> listarDisponibles(@PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}/disponibles", juegoId);
        return ResponseEntity.ok(licenciaService.listarDisponiblesPorJuego(juegoId));
    }

    // GET /api/licencias/juego/{juegoId}/stock
    @GetMapping("/juego/{juegoId}/stock")
    public ResponseEntity<Map<String, Object>> contarStock(@PathVariable Long juegoId) {
        log.info("GET /api/licencias/juego/{}/stock", juegoId);
        return ResponseEntity.ok(Map.of(
                "juegoId", juegoId,
                "disponibles", licenciaService.contarDisponiblesPorJuego(juegoId)
        ));
    }

    // POST /api/licencias/asignar — usado por servicio de pedidos
    @PostMapping("/asignar")
    public ResponseEntity<LicenciaResponse> asignar(
            @RequestParam Long juegoId,
            @RequestParam Long pedidoId) {
        log.info("POST /api/licencias/asignar - juegoId: {}, pedidoId: {}", juegoId, pedidoId);
        return ResponseEntity.ok(licenciaService.asignarLicencia(juegoId, pedidoId));
    }

    // PATCH /api/licencias/{id}/revocar
    @PatchMapping("/{id}/revocar")
    public ResponseEntity<LicenciaResponse> revocar(@PathVariable Long id) {
        log.info("PATCH /api/licencias/{}/revocar", id);
        return ResponseEntity.ok(licenciaService.revocarLicencia(id));
    }

    // GET /api/licencias/validar?codigo= — usado por otros microservicios
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String codigo) {
        log.info("GET /api/licencias/validar?codigo={}", codigo);
        return ResponseEntity.ok(Map.of("valido", licenciaService.validarCodigo(codigo)));
    }
}