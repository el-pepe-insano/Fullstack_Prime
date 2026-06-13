package nopki.resenas.controller;

import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import nopki.resenas.service.ResenaService;
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
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenasController {

    private static final Logger log = LoggerFactory.getLogger(ResenasController.class);

    private final ResenaService resenaService;

    // POST /api/resenas
    @PostMapping
    public ResponseEntity<ResenaResponse> crear(@Valid @RequestBody ResenaRequest request) {
        log.info("POST /api/resenas - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request));
    }

    // GET /api/resenas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    // GET /api/resenas/juego/{juegoId}
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<ResenaResponse>> listarPorJuego(@PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}", juegoId);
        return ResponseEntity.ok(resenaService.listarPorJuego(juegoId));
    }

    // GET /api/resenas/juego/{juegoId}/ordenadas
    @GetMapping("/juego/{juegoId}/ordenadas")
    public ResponseEntity<List<ResenaResponse>> listarPorJuegoOrdenadas(@PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}/ordenadas", juegoId);
        return ResponseEntity.ok(resenaService.listarPorJuegoOrdenadas(juegoId));
    }

    // GET /api/resenas/juego/{juegoId}/estadisticas
    @GetMapping("/juego/{juegoId}/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticasJuego(@PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}/estadisticas", juegoId);
        return ResponseEntity.ok(resenaService.estadisticasJuego(juegoId));
    }

    // GET /api/resenas/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponse>> listarPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/resenas/usuario/{}", usuarioId);
        return ResponseEntity.ok(resenaService.listarPorUsuario(usuarioId));
    }

    // GET /api/resenas/usuario/{usuarioId}/juego/{juegoId}
    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<ResenaResponse> obtenerPorUsuarioYJuego(
            @PathVariable Long usuarioId,
            @PathVariable Long juegoId) {
        log.info("GET /api/resenas/usuario/{}/juego/{}", usuarioId, juegoId);
        return ResponseEntity.ok(resenaService.obtenerPorUsuarioYJuego(usuarioId, juegoId));
    }

    // GET /api/resenas/calificacion/{calificacion}
    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<ResenaResponse>> listarPorCalificacion(@PathVariable Integer calificacion) {
        log.info("GET /api/resenas/calificacion/{}", calificacion);
        return ResponseEntity.ok(resenaService.listarPorCalificacion(calificacion));
    }

    // PUT /api/resenas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        log.info("PUT /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    // DELETE /api/resenas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/resenas/{}", id);
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}