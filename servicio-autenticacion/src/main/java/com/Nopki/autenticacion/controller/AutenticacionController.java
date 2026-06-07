package com.Nopki.autenticacion.controller;

import com.Nopki.autenticacion.dto.*;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.service.UsuarioService;
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
public class AutenticacionController {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionController.class);

    private final UsuarioService usuarioService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - {}", request.getEmail());
        return ResponseEntity.ok(usuarioService.login(request));
    }

    // POST /api/auth/registro
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        log.info("POST /api/auth/registro - {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(request));
    }

    // GET /api/auth/usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listarTodos() {
        log.info("GET /api/auth/usuarios");
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET /api/auth/usuarios/{id}
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        log.info("GET /api/auth/usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    // GET /api/auth/usuarios/email/{email}
    @GetMapping("/usuarios/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(@PathVariable String email) {
        log.info("GET /api/auth/usuarios/email/{}", email);
        return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
    }

    // GET /api/auth/usuarios/rol/{rol}
    @GetMapping("/usuarios/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(@PathVariable Rol rol) {
        log.info("GET /api/auth/usuarios/rol/{}", rol);
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    // PATCH /api/auth/usuarios/{id}/estado
    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<UsuarioResponse> actualizarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        log.info("PATCH /api/auth/usuarios/{}/estado - activo: {}", id, activo);
        return ResponseEntity.ok(usuarioService.actualizarEstado(id, activo));
    }

    // GET /api/auth/validar-token — usado por otros microservicios vía WebClient
    @GetMapping("/validar-token")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestParam String token) {
        log.info("GET /api/auth/validar-token");
        boolean valido = usuarioService.validarToken(token);
        return ResponseEntity.ok(Map.of("valido", valido));
    }

    // GET /api/auth/existe/{id} — usado por otros microservicios vía WebClient
    @GetMapping("/existe/{id}")
    public ResponseEntity<Map<String, Object>> existeUsuario(@PathVariable Long id) {
        log.info("GET /api/auth/existe/{}", id);
        return ResponseEntity.ok(Map.of("existe", usuarioService.existeUsuario(id)));
    }
}