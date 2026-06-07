package com.Nopki.autenticacion.service;

import com.Nopki.autenticacion.dto.*;
import com.Nopki.autenticacion.exception.UsuarioNoEncontradoException;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.model.Usuario;
import com.Nopki.autenticacion.repository.UsuarioRepository;
import com.Nopki.autenticacion.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para: {}", request.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Credenciales inválidas"));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(usuario);
        log.info("Login exitoso para: {}", request.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .rol(request.getRol())
                .build();

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado con id: {}", guardado.getId());

        return UsuarioResponse.desde(guardado);
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        return UsuarioResponse.desde(buscarPorId(id));
    }

    @Override
    public UsuarioResponse obtenerPorEmail(String email) {
        log.info("Buscando usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email));
        return UsuarioResponse.desde(usuario);
    }

    @Override
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioResponse::desde)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> listarPorRol(Rol rol) {
        log.info("Listando usuarios con rol: {}", rol);
        return usuarioRepository.findByRol(rol)
                .stream()
                .map(UsuarioResponse::desde)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse actualizarEstado(Long id, boolean activo) {
        log.info("Actualizando estado del usuario {} a: {}", id, activo);
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(activo);
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Override
    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }

    @Override
    public boolean existeUsuario(Long id) {
        return usuarioRepository.existsById(id);
    }

    private Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
    }
}