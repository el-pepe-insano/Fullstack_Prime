package com.nopki.biblioteca.controller;

import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import com.nopki.biblioteca.service.BibliotecaService;
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
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
@Tag(name = "Biblioteca", description = "Gestion de la biblioteca de juegos adquiridos por cada usuario")
public class BibliotecaController {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaController.class);

    private final BibliotecaService bibliotecaService;

    @Operation(summary = "Agregar juego a biblioteca", description = "Registra un juego en la biblioteca del usuario tras una compra exitosa, validando que el usuario exista y no tenga el juego ya registrado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Juego agregado exitosamente a la biblioteca"),
            @ApiResponse(responseCode = "400", description = "El usuario no existe"),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene este juego en su biblioteca")
    })
    @PostMapping
    public ResponseEntity<BibliotecaResponse> agregar(@Valid @RequestBody BibliotecaRequest request) {
        log.info("POST /api/biblioteca - usuario: {}, juego: {}", request.getUsuarioId(), request.getJuegoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bibliotecaService.agregar(request));
    }

    @Operation(summary = "Obtener entrada por id", description = "Busca una entrada de biblioteca especifica por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "Entrada no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BibliotecaResponse> obtenerPorId(@Parameter(description = "ID de la entrada") @PathVariable Long id) {
        log.info("GET /api/biblioteca/{}", id);
        return ResponseEntity.ok(bibliotecaService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener biblioteca de un usuario", description = "Retorna todos los juegos adquiridos por un usuario especifico")
    @ApiResponse(responseCode = "200", description = "Biblioteca obtenida exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<BibliotecaResponse>> obtenerBibliotecaUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}", usuarioId);
        return ResponseEntity.ok(bibliotecaService.obtenerBibliotecaUsuario(usuarioId));
    }

    @Operation(summary = "Obtener juego especifico de un usuario", description = "Busca un juego especifico dentro de la biblioteca de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Juego encontrado en la biblioteca"),
            @ApiResponse(responseCode = "404", description = "El usuario no posee ese juego")
    })
    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<BibliotecaResponse> obtenerJuegoDeUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/biblioteca/usuario/{}/juego/{}", usuarioId, juegoId);
        return ResponseEntity.ok(bibliotecaService.obtenerJuegoDeUsuario(usuarioId, juegoId));
    }

    @Operation(summary = "Verificar posesion de juego", description = "Endpoint interno usado por el servicio de resenas via WebClient para validar que el usuario compro el juego antes de permitirle reseñarlo")
    @ApiResponse(responseCode = "200", description = "Resultado de la verificacion")
    @GetMapping("/usuario/{usuarioId}/posee/{juegoId}")
    public ResponseEntity<Map<String, Object>> usuarioPoseeJuego(
            @Parameter(description = "ID del usuario") @PathVariable Long usuarioId,
            @Parameter(description = "ID del juego") @PathVariable Long juegoId) {
        log.info("GET /api/biblioteca/usuario/{}/posee/{}", usuarioId, juegoId);
        return ResponseEntity.ok(Map.of(
                "posee", bibliotecaService.usuarioPoseeJuego(usuarioId, juegoId)
        ));
    }

    @Operation(summary = "Contar juegos de un usuario", description = "Retorna la cantidad total de juegos en la biblioteca de un usuario")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido exitosamente")
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<Map<String, Object>> contarJuegos(@Parameter(description = "ID del usuario") @PathVariable Long usuarioId) {
        log.info("GET /api/biblioteca/usuario/{}/total", usuarioId);
        return ResponseEntity.ok(Map.of(
                "usuarioId", usuarioId,
                "total", bibliotecaService.contarJuegosDeUsuario(usuarioId)
        ));
    }

    @Operation(summary = "Eliminar entrada de biblioteca", description = "Elimina un juego de la biblioteca de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Entrada eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Entrada no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Parameter(description = "ID de la entrada") @PathVariable Long id) {
        log.info("DELETE /api/biblioteca/{}", id);
        bibliotecaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}