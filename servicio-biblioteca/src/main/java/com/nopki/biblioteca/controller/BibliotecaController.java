package com.nopki.biblioteca.controller;

import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import com.nopki.biblioteca.service.BibliotecaService;
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
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
public class BibliotecaController {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaController.class);

    private final BibliotecaService bibliotecaService;

    // POST /api/biblioteca
    @PostMapping
    public ResponseEntity<BibliotecaResponse> agregar(@Valid @RequestBody BibliotecaRequest request) {
        log.info("POST /api/biblioteca - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bibliotecaService.agregar(request));
    }

    // GET /api/biblioteca/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BibliotecaResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/biblioteca/{}", id);
        return ResponseEntity.ok(bibliotecaService.obtenerPorId(id));
    }

    // GET /api/biblioteca/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<BibliotecaResponse>> obtenerBibliotecaUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}", usuarioId);
        return ResponseEntity.ok(bibliotecaService.obtenerBibliotecaUsuario(usuarioId));
    }

    // GET /api/biblioteca/usuario/{usuarioId}/juego/{juegoId}
    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<BibliotecaResponse> obtenerJuegoDeUsuario(
            @PathVariable Long usuarioId,
            @PathVariable Long juegoId) {
        log.info("GET /api/biblioteca/usuario/{}/juego/{}", usuarioId, juegoId);
        return ResponseEntity.ok(bibliotecaService.obtenerJuegoDeUsuario(usuarioId, juegoId));
    }

    // GET /api/biblioteca/usuario/{usuarioId}/posee/{juegoId} — usado por servicio de reseñas
    @GetMapping("/usuario/{usuarioId}/posee/{juegoId}")
    public ResponseEntity<Map<String, Object>> usuarioPoseeJuego(
            @PathVariable Long usuarioId,
            @PathVariable Long juegoId) {
        log.info("GET /api/biblioteca/usuario/{}/posee/{}", usuarioId, juegoId);
        return ResponseEntity.ok(Map.of(
                "posee", bibliotecaService.usuarioPoseeJuego(usuarioId, juegoId)
        ));
    }

    // GET /api/biblioteca/usuario/{usuarioId}/total
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<Map<String, Object>> contarJuegos(@PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}/total", usuarioId);
        return ResponseEntity.ok(Map.of(
                "usuarioId", usuarioId,
                "total", bibliotecaService.contarJuegosDeUsuario(usuarioId)
        ));
    }

    // DELETE /api/biblioteca/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/biblioteca/{}", id);
        bibliotecaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}