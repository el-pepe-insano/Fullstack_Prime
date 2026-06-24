package com.Nopki.autenticacion.service;

import com.Nopki.autenticacion.dto.LoginRequest;
import com.Nopki.autenticacion.dto.LoginResponse;
import com.Nopki.autenticacion.dto.RegistroRequest;
import com.Nopki.autenticacion.exception.UsuarioNoEncontradoException;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.model.Usuario;
import com.Nopki.autenticacion.repository.UsuarioRepository;
import com.Nopki.autenticacion.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Perez")
                .email("juan@gmail.com")
                .contrasena("encriptada")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();
    }

    @Test
    void login_debeRetornarTokenCuandoCredencialesValidas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@gmail.com");
        request.setContrasena("password123");

        when(usuarioRepository.findByEmail("juan@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "encriptada")).thenReturn(true);
        when(jwtUtil.generarToken(usuario)).thenReturn("token-jwt-simulado");

        LoginResponse response = usuarioService.login(request);

        assertNotNull(response);
        assertEquals("token-jwt-simulado", response.getToken());
        assertEquals("juan@gmail.com", response.getEmail());
        verify(usuarioRepository, times(1)).findByEmail("juan@gmail.com");
    }

    @Test
    void login_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        LoginRequest request = new LoginRequest();
        request.setEmail("noexiste@gmail.com");
        request.setContrasena("password123");

        when(usuarioRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.login(request));
    }

    @Test
    void login_debeLanzarExcepcionCuandoContrasenaIncorrecta() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@gmail.com");
        request.setContrasena("incorrecta");

        when(usuarioRepository.findByEmail("juan@gmail.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "encriptada")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.login(request));
    }

    @Test
    void login_debeLanzarExcepcionCuandoUsuarioInactivo() {
        usuario.setActivo(false);
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@gmail.com");
        request.setContrasena("password123");

        when(usuarioRepository.findByEmail("juan@gmail.com")).thenReturn(Optional.of(usuario));

        assertThrows(IllegalStateException.class, () -> usuarioService.login(request));
    }

    @Test
    void registrar_debeCrearUsuarioCuandoEmailNoExiste() {
        RegistroRequest request = new RegistroRequest();
        request.setNombre("Nuevo Usuario");
        request.setEmail("nuevo@gmail.com");
        request.setContrasena("password123");
        request.setRol(Rol.CLIENTE);

        when(usuarioRepository.existsByEmail("nuevo@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encriptada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        var response = usuarioService.registrar(request);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registrar_debeLanzarExcepcionCuandoEmailYaExiste() {
        RegistroRequest request = new RegistroRequest();
        request.setEmail("juan@gmail.com");
        request.setNombre("Juan");
        request.setContrasena("password123");
        request.setRol(Rol.CLIENTE);

        when(usuarioRepository.existsByEmail("juan@gmail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.registrar(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void obtenerPorId_debeRetornarUsuarioCuandoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var response = usuarioService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals("Juan Perez", response.getNombre());
    }

    @Test
    void obtenerPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    void existeUsuario_debeRetornarTrueCuandoExiste() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        assertTrue(usuarioService.existeUsuario(1L));
    }
}