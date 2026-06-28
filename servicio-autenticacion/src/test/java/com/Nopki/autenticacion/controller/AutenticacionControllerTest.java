package com.Nopki.autenticacion.controller;

import com.Nopki.autenticacion.dto.*;
import com.Nopki.autenticacion.model.Rol;
import com.Nopki.autenticacion.security.SecurityConfig;
import com.Nopki.autenticacion.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutenticacionController.class)
@Import(SecurityConfig.class)
class AutenticacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_debeRetornar200ConTokenValido() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@gmail.com");
        request.setContrasena("password123");

        LoginResponse response = LoginResponse.builder()
                .token("token-simulado")
                .tipo("Bearer")
                .usuarioId(1L)
                .nombre("Juan Perez")
                .email("juan@gmail.com")
                .rol(Rol.CLIENTE)
                .build();

        when(usuarioService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-simulado"))
                .andExpect(jsonPath("$.email").value("juan@gmail.com"));
    }

    @Test
    void login_debeRetornar400CuandoEmailInvalido() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("no-es-un-email");
        request.setContrasena("password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_debeRetornar201CuandoDatosValidos() throws Exception {
        RegistroRequest request = new RegistroRequest();
        request.setNombre("Nuevo Usuario");
        request.setEmail("nuevo@gmail.com");
        request.setContrasena("password123");
        request.setRol(Rol.CLIENTE);

        UsuarioResponse response = UsuarioResponse.builder()
                .id(1L)
                .nombre("Nuevo Usuario")
                .email("nuevo@gmail.com")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();

        when(usuarioService.registrar(any(RegistroRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo Usuario"));
    }

    @Test
    void registrar_debeRetornar400CuandoContrasenaCorta() throws Exception {
        RegistroRequest request = new RegistroRequest();
        request.setNombre("Nuevo Usuario");
        request.setEmail("nuevo@gmail.com");
        request.setContrasena("123");
        request.setRol(Rol.CLIENTE);

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodos_debeRetornar200ConListaDeUsuarios() throws Exception {
        UsuarioResponse usuario = UsuarioResponse.builder()
                .id(1L).nombre("Juan").email("juan@gmail.com").rol(Rol.CLIENTE).activo(true).build();

        when(usuarioService.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() throws Exception {
        UsuarioResponse usuario = UsuarioResponse.builder()
                .id(1L).nombre("Juan").email("juan@gmail.com").rol(Rol.CLIENTE).activo(true).build();

        when(usuarioService.obtenerPorId(1L)).thenReturn(usuario);

        mockMvc.perform(get("/api/auth/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void validarToken_debeRetornar200ConResultado() throws Exception {
        when(usuarioService.validarToken("token-x")).thenReturn(true);

        mockMvc.perform(get("/api/auth/validar-token").param("token", "token-x"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    void existeUsuario_debeRetornar200ConResultado() throws Exception {
        when(usuarioService.existeUsuario(1L)).thenReturn(true);

        mockMvc.perform(get("/api/auth/existe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));
    }

    @Test
    void listarPorRol_debeRetornar200ConListaFiltrada() throws Exception {
        UsuarioResponse usuario = UsuarioResponse.builder()
                .id(1L).nombre("Admin").email("admin@nopki.com").rol(Rol.ADMIN).activo(true).build();

        when(usuarioService.listarPorRol(Rol.ADMIN)).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/auth/usuarios/rol/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rol").value("ADMIN"));
    }
}