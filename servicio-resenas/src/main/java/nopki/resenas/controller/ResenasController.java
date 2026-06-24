package nopki.resenas.controller;

import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import nopki.resenas.service.ResenaService;
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
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Resenas", description = "Gestion de resenas y calificaciones de juegos")
public class ResenasController {

    private static final Logger log = LoggerFactory.getLogger(ResenasController.class);

    private final ResenaService resenaService;

    @Operation(summary = "Crear resena", description = "Crea una resena validando que el usuario exista, el juego exista, el usuario haya comprado el juego, y no tenga ya una resena previa para ese juego")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resena creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "El usuario o el juego no existen"),
            @ApiResponse(responseCode = "409", description = "El usuario no posee el juego o ya tiene una resena registrada")
    })
    @PostMapping
    public ResponseEntity<ResenaResponse> crear(@Valid @RequestBody ResenaRequest request) {
        log.info("POST /api/resenas - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(request));
    }

    @Operation(summary = "Obtener resena por id", description = "Busca una resena especifica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resena encontrada"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponse> obtenerPorId(@Parameter(description = "ID de la resena") @PathVariable Long id) {
        log.info("GET /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.obtenerPorId(id));
    }

    @Operation(summary = "Listar resenas de un juego", description = "Retorna todas las resenas asociadas a un juego especifico")
    @ApiResponse(responseCode = "200", description = "Lista de resenas obtenida exitosamente")
    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<ResenaResponse>> listarPorJuego(@Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}", juegoId);
        return ResponseEntity.ok(resenaService.listarPorJuego(juegoId));
    }

    @Operation(summary = "Listar resenas de un juego ordenadas", description = "Retorna las resenas de un juego ordenadas de mayor a menor calificacion")
    @ApiResponse(responseCode = "200", description = "Lista ordenada obtenida exitosamente")
    @GetMapping("/juego/{juegoId}/ordenadas")
    public ResponseEntity<List<ResenaResponse>> listarPorJuegoOrdenadas(@Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}/ordenadas", juegoId);
        return ResponseEntity.ok(resenaService.listarPorJuegoOrdenadas(juegoId));
    }

    @Operation(summary = "Estadisticas de resenas de un juego", description = "Retorna el promedio de calificacion y el total de resenas de un juego")
    @ApiResponse(responseCode = "200", description = "Estadisticas obtenidas exitosamente")
    @GetMapping("/juego/{juegoId}/estadisticas")
    public ResponseEntity<Map<String, Object>> estadisticasJuego(@Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/resenas/juego/{}/estadisticas", juegoId);
        return ResponseEntity.ok(resenaService.estadisticasJuego(juegoId));
    }

    @Operation(summary = "Listar resenas de un usuario", description = "Retorna todas las resenas creadas por un usuario especifico")
    @ApiResponse(responseCode = "200", description = "Lista de resenas obtenida exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponse>> listarPorUsuario(@Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/resenas/usuario/{}", usuarioId);
        return ResponseEntity.ok(resenaService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Obtener resena especifica de usuario y juego", description = "Busca la resena de un usuario para un juego en particular")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resena encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe resena de ese usuario para ese juego")
    })
    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<ResenaResponse> obtenerPorUsuarioYJuego(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/resenas/usuario/{}/juego/{}", usuarioId, juegoId);
        return ResponseEntity.ok(resenaService.obtenerPorUsuarioYJuego(usuarioId, juegoId));
    }

    @Operation(summary = "Listar resenas por calificacion", description = "Filtra resenas segun una calificacion especifica (1 a 5)")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/calificacion/{calificacion}")
    public ResponseEntity<List<ResenaResponse>> listarPorCalificacion(@Parameter(description = "Calificacion a filtrar (1-5)") @PathVariable Integer calificacion) {
        log.info("GET /api/resenas/calificacion/{}", calificacion);
        return ResponseEntity.ok(resenaService.listarPorCalificacion(calificacion));
    }

    @Operation(summary = "Actualizar resena", description = "Actualiza la calificacion y comentario de una resena, solo permitido para el autor original")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resena actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada"),
            @ApiResponse(responseCode = "409", description = "No tienes permiso para modificar esta resena")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponse> actualizar(
            @Parameter(description = "ID de la resena") @PathVariable Long id,
            @Valid @RequestBody ResenaRequest request) {
        log.info("PUT /api/resenas/{}", id);
        return ResponseEntity.ok(resenaService.actualizar(id, request));
    }

    @Operation(summary = "Eliminar resena", description = "Elimina una resena de forma permanente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Resena eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Resena no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID de la resena") @PathVariable Long id) {
        log.info("DELETE /api/resenas/{}", id);
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}