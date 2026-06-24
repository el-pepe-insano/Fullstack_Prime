package com.nopki.catalogo.controller;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.service.JuegoService;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@Tag(name = "Catalogo", description = "Gestion del catalogo de videojuegos de Nopki")
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);

    private final JuegoService juegoService;

    @Operation(summary = "Crear juego", description = "Registra un nuevo juego en el catalogo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    @PostMapping("/juegos")
    public ResponseEntity<JuegoResponse> crear(@Valid @RequestBody JuegoRequest request) {
        log.info("POST /api/catalogo/juegos - {}", request.getTitulo());
        return ResponseEntity.status(HttpStatus.CREATED).body(juegoService.crear(request));
    }

    @Operation(summary = "Listar todos los juegos", description = "Retorna el catalogo completo de juegos")
    @ApiResponse(responseCode = "200", description = "Lista de juegos obtenida exitosamente")
    @GetMapping("/juegos")
    public ResponseEntity<List<JuegoResponse>> listarTodos() {
        log.info("GET /api/catalogo/juegos");
        return ResponseEntity.ok(juegoService.listarTodos());
    }

    @Operation(summary = "Listar juegos disponibles", description = "Retorna solo los juegos marcados como disponibles para la venta")
    @ApiResponse(responseCode = "200", description = "Lista de juegos disponibles obtenida exitosamente")
    @GetMapping("/juegos/disponibles")
    public ResponseEntity<List<JuegoResponse>> listarDisponibles() {
        log.info("GET /api/catalogo/juegos/disponibles");
        return ResponseEntity.ok(juegoService.listarDisponibles());
    }

    @Operation(summary = "Obtener juego por id", description = "Busca un juego especifico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @GetMapping("/juegos/{id}")
    public ResponseEntity<JuegoResponse> obtenerPorId(
            @Parameter(description = "ID del juego") @PathVariable Long id) {
        log.info("GET /api/catalogo/juegos/{}", id);
        return ResponseEntity.ok(juegoService.obtenerPorId(id));
    }

    @Operation(summary = "Buscar juegos por titulo", description = "Busca juegos cuyo titulo coincida parcialmente con el texto ingresado")
    @ApiResponse(responseCode = "200", description = "Resultados de busqueda obtenidos exitosamente")
    @GetMapping("/juegos/buscar")
    public ResponseEntity<List<JuegoResponse>> buscarPorTitulo(
            @Parameter(description = "Texto a buscar en el titulo") @RequestParam String titulo) {
        log.info("GET /api/catalogo/juegos/buscar?titulo={}", titulo);
        return ResponseEntity.ok(juegoService.buscarPorTitulo(titulo));
    }

    @Operation(summary = "Listar juegos por genero", description = "Filtra los juegos disponibles segun su genero")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/juegos/genero/{genero}")
    public ResponseEntity<List<JuegoResponse>> listarPorGenero(
            @Parameter(description = "Genero a filtrar") @PathVariable Genero genero) {
        log.info("GET /api/catalogo/juegos/genero/{}", genero);
        return ResponseEntity.ok(juegoService.listarPorGenero(genero));
    }

    @Operation(summary = "Listar juegos por plataforma", description = "Filtra los juegos segun la plataforma especificada")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/juegos/plataforma/{plataforma}")
    public ResponseEntity<List<JuegoResponse>> listarPorPlataforma(
            @Parameter(description = "Plataforma a filtrar") @PathVariable String plataforma) {
        log.info("GET /api/catalogo/juegos/plataforma/{}", plataforma);
        return ResponseEntity.ok(juegoService.listarPorPlataforma(plataforma));
    }

    @Operation(summary = "Buscar juegos por rango de precio", description = "Filtra juegos disponibles dentro de un rango de precio minimo y maximo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos exitosamente"),
            @ApiResponse(responseCode = "400", description = "El precio minimo es mayor al maximo")
    })
    @GetMapping("/juegos/precio")
    public ResponseEntity<List<JuegoResponse>> buscarPorPrecio(
            @Parameter(description = "Precio minimo") @RequestParam BigDecimal min,
            @Parameter(description = "Precio maximo") @RequestParam BigDecimal max) {
        log.info("GET /api/catalogo/juegos/precio?min={}&max={}", min, max);
        return ResponseEntity.ok(juegoService.buscarPorRangoPrecio(min, max));
    }

    @Operation(summary = "Actualizar juego", description = "Actualiza todos los datos de un juego existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @PutMapping("/juegos/{id}")
    public ResponseEntity<JuegoResponse> actualizar(
            @Parameter(description = "ID del juego") @PathVariable Long id,
            @Valid @RequestBody JuegoRequest request) {
        log.info("PUT /api/catalogo/juegos/{}", id);
        return ResponseEntity.ok(juegoService.actualizar(id, request));
    }

    @Operation(summary = "Cambiar disponibilidad del juego", description = "Marca un juego como disponible o no disponible para la venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @PatchMapping("/juegos/{id}/disponibilidad")
    public ResponseEntity<JuegoResponse> actualizarDisponibilidad(
            @Parameter(description = "ID del juego") @PathVariable Long id,
            @Parameter(description = "Nueva disponibilidad") @RequestParam boolean disponible) {
        log.info("PATCH /api/catalogo/juegos/{}/disponibilidad - {}", id, disponible);
        return ResponseEntity.ok(juegoService.actualizarDisponibilidad(id, disponible));
    }

    @Operation(summary = "Eliminar juego", description = "Elimina un juego del catalogo de forma permanente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Juego eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Juego no encontrado")
    })
    @DeleteMapping("/juegos/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID del juego") @PathVariable Long id) {
        log.info("DELETE /api/catalogo/juegos/{}", id);
        juegoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verificar existencia de juego", description = "Endpoint interno usado por otros microservicios via WebClient para validar que un juego existe")
    @ApiResponse(responseCode = "200", description = "Resultado de la verificacion")
    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Object>> existeJuego(
            @Parameter(description = "ID del juego a verificar") @PathVariable Long id) {
        log.info("GET /api/catalogo/existe/{}", id);
        return ResponseEntity.ok(Map.of("existe", juegoService.existeJuego(id)));
    }
}