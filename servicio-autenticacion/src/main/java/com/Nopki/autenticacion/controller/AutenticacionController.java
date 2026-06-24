package com.Nopki.autenticacion.controller;

import com.Nopki.autenticacion.dto.*;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.service.UsuarioService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Gestion de usuarios, roles y autenticacion JWT")
public class AutenticacionController {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionController.class);

    private final UsuarioService usuarioService;

    @Operation(summary = "Iniciar sesion", description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
            @ApiResponse(responseCode = "400", description = "Credenciales invalidas"),
            @ApiResponse(responseCode = "403", description = "Cuenta desactivada"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - {}", request.getEmail());
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema con contrasena encriptada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El email ya esta registrado o datos invalidos")
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        log.info("POST /api/auth/registro - {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    @Operation(summary = "Listar todos los usuarios", description = "Retorna la lista completa de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        log.info("GET /api/auth/usuarios");
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Obtener usuario por id", description = "Busca un usuario especifico por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        log.info("GET /api/auth/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener usuario por email", description = "Busca un usuario especifico por su correo electronico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(
            @Parameter(description = "Email del usuario") @PathVariable String email) {
        log.info("GET /api/auth/usuarios/email/{}", email);
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    @Operation(summary = "Listar usuarios por rol", description = "Filtra usuarios segun su rol asignado (ADMIN, CLIENTE, OPERADOR)")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida exitosamente")
    @GetMapping("/usuarios/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(
            @Parameter(description = "Rol a filtrar") @PathVariable Rol rol) {
        log.info("GET /api/auth/usuarios/rol/{}", rol);
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @Operation(summary = "Validar token JWT", description = "Endpoint interno usado por otros microservicios via WebClient para validar tokens")
    @ApiResponse(responseCode = "200", description = "Resultado de la validacion del token")
    @GetMapping("/validar-token")
    public ResponseEntity<Map<String, Object>> validarToken(
            @Parameter(description = "Token JWT a validar") @RequestParam String token) {
        log.info("GET /api/auth/validar-token");
        boolean valido = usuarioService.validarToken(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    @Operation(summary = "Verificar existencia de usuario", description = "Endpoint interno usado por otros microservicios via WebClient para validar que un usuario existe")
    @ApiResponse(responseCode = "200", description = "Resultado de la verificacion")
    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Object>> existeUsuario(
            @Parameter(description = "ID del usuario a verificar") @PathVariable Long id) {
        log.info("GET /api/auth/existe/{}", id);
        return ResponseEntity.ok(Map.of("existe", usuarioService.existeUsuario(id)));
    }
}