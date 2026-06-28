package com.nopki.biblioteca.controller;

import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import com.nopki.biblioteca.service.BibliotecaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BibliotecaController.class)
class BibliotecaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BibliotecaService bibliotecaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void agregar_debeRetornar201CuandoDatosValidos() throws Exception {
        BibliotecaRequest request = new BibliotecaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCodigoLicencia("NOPKI-RE04-J0K1-L2M3");
        request.setPedidoId(1L);

        BibliotecaResponse response = BibliotecaResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).tituloJuego("Resident Evil 4")
                .codigoLicencia("NOPKI-RE04-J0K1-L2M3").pedidoId(1L).build();

        when(bibliotecaService.agregar(any(BibliotecaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/biblioteca")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tituloJuego").value("Resident Evil 4"));
    }

    @Test
    void obtenerBibliotecaUsuario_debeRetornar200ConLista() throws Exception {
        BibliotecaResponse response = BibliotecaResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).tituloJuego("Resident Evil 4").build();

        when(bibliotecaService.obtenerBibliotecaUsuario(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/biblioteca/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].juegoId").value(4));
    }

    @Test
    void usuarioPoseeJuego_debeRetornar200ConResultado() throws Exception {
        when(bibliotecaService.usuarioPoseeJuego(2L, 4L)).thenReturn(true);

        mockMvc.perform(get("/api/biblioteca/usuario/2/posee/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posee").value(true));
    }

    @Test
    void contarJuegos_debeRetornar200ConTotal() throws Exception {
        when(bibliotecaService.contarJuegosDeUsuario(2L)).thenReturn(3L);

        mockMvc.perform(get("/api/biblioteca/usuario/2/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/biblioteca/1"))
                .andExpect(status().isNoContent());
    }
}