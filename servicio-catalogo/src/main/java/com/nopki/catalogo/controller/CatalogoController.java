package com.nopki.catalogo.controller;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.service.JuegoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);

    private final JuegoService juegoService;

    // POST /api/catalogo/juegos
    @PostMapping("/juegos")
    public ResponseEntity<JuegoResponse> crear(@Valid @RequestBody JuegoRequest request) {
        log.info("POST /api/catalogo/juegos - {}", request.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.crear(request));
    }

    // GET /api/catalogo/juegos
    @GetMapping("/juegos")
    public ResponseEntity<List<JuegoResponse>> listarTodos() {
        log.info("GET /api/catalogo/juegos");
        return ResponseEntity.ok(juegoService.listarTodos());
    }

    // GET /api/catalogo/juegos/disponibles
    @GetMapping("/juegos/disponibles")
    public ResponseEntity<List<JuegoResponse>> listarDisponibles() {
        log.info("GET /api/catalogo/juegos/disponibles");
        return ResponseEntity.ok(juegoService.listarDisponibles());
    }

    // GET /api/catalogo/juegos/{id}
    @GetMapping("/juegos/{id}")
    public ResponseEntity<JuegoResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/catalogo/juegos/{}", id);
        return ResponseEntity.ok(juegoService.obtenerPorId(id));
    }

    // GET /api/catalogo/juegos/buscar?titulo=
    @GetMapping("/juegos/buscar")
    public ResponseEntity<List<JuegoResponse>> buscarPorTitulo(@RequestParam String titulo) {
        log.info("GET /api/catalogo/juegos/buscar?titulo={}", titulo);
        return ResponseEntity.ok(juegoService.buscarPorTitulo(titulo));
    }

    // GET /api/catalogo/juegos/genero/{genero}
    @GetMapping("/juegos/genero/{genero}")
    public ResponseEntity<List<JuegoResponse>> listarPorGenero(@PathVariable Genero genero) {
        log.info("GET /api/catalogo/juegos/genero/{}", genero);
        return ResponseEntity.ok(juegoService.listarPorGenero(genero));
    }

    // GET /api/catalogo/juegos/plataforma/{plataforma}
    @GetMapping("/juegos/plataforma/{plataforma}")
    public ResponseEntity<List<JuegoResponse>> listarPorPlataforma(@PathVariable String plataforma) {
        log.info("GET /api/catalogo/juegos/plataforma/{}", plataforma);
        return ResponseEntity.ok(juegoService.listarPorPlataforma(plataforma));
    }

    // GET /api/catalogo/juegos/precio?min=&max=
    @GetMapping("/juegos/precio")
    public ResponseEntity<List<JuegoResponse>> buscarPorPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        log.info("GET /api/catalogo/juegos/precio?min={}&max={}", min, max);
        return ResponseEntity.ok(juegoService.buscarPorRangoPrecio(min, max));
    }

    // PUT /api/catalogo/juegos/{id}
    @PutMapping("/juegos/{id}")
    public ResponseEntity<JuegoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody JuegoRequest request) {
        log.info("PUT /api/catalogo/juegos/{}", id);
        return ResponseEntity.ok(juegoService.actualizar(id, request));
    }

    // PATCH /api/catalogo/juegos/{id}/disponibilidad
    @PatchMapping("/juegos/{id}/disponibilidad")
    public ResponseEntity<JuegoResponse> actualizarDisponibilidad(
            @PathVariable Long id,
            @RequestParam boolean disponible) {
        log.info("PATCH /api/catalogo/juegos/{}/disponibilidad - {}", id, disponible);
        return ResponseEntity.ok(juegoService.actualizarDisponibilidad(id, disponible));
    }

    // DELETE /api/catalogo/juegos/{id}
    @DeleteMapping("/juegos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("DELETE /api/catalogo/juegos/{}", id);
        juegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/catalogo/existe/{id} — usado por otros microservicios vía WebClient
    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Object>> existeJuego(@PathVariable Long id) {
        log.info("GET /api/catalogo/existe/{}", id);
        return ResponseEntity.ok(Map.of("existe", juegoService.existeJuego(id)));
    }
}