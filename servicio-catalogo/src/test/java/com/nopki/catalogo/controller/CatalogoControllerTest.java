package com.nopki.catalogo.controller;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.service.JuegoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogoController.class)
class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JuegoService juegoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crear_debeRetornar201CuandoDatosValidos() throws Exception {
        JuegoRequest request = new JuegoRequest();
        request.setTitulo("Resident Evil 4");
        request.setDescripcion("Leon S. Kennedy viaja a Europa para rescatar a la hija del presidente");
        request.setPrecio(new BigDecimal("19.99"));
        request.setDesarrollador("Capcom");
        request.setPlataforma("PC");
        request.setGenero(Genero.ACCION);

        JuegoResponse response = JuegoResponse.builder()
                .id(1L).titulo("Resident Evil 4").precio(new BigDecimal("19.99")).disponible(true).build();

        when(juegoService.crear(any(JuegoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/catalogo/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Resident Evil 4"));
    }

    @Test
    void crear_debeRetornar400CuandoPrecioInvalido() throws Exception {
        JuegoRequest request = new JuegoRequest();
        request.setTitulo("Juego");
        request.setDescripcion("Descripcion");
        request.setPrecio(new BigDecimal("-5.00"));
        request.setDesarrollador("Dev");
        request.setPlataforma("PC");
        request.setGenero(Genero.ACCION);

        mockMvc.perform(post("/api/catalogo/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarDisponibles_debeRetornar200ConLista() throws Exception {
        JuegoResponse juego = JuegoResponse.builder().id(1L).titulo("Resident Evil 4").disponible(true).build();

        when(juegoService.listarDisponibles()).thenReturn(List.of(juego));

        mockMvc.perform(get("/api/catalogo/juegos/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Resident Evil 4"));
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() throws Exception {
        JuegoResponse juego = JuegoResponse.builder().id(1L).titulo("Resident Evil 4").build();

        when(juegoService.obtenerPorId(1L)).thenReturn(juego);

        mockMvc.perform(get("/api/catalogo/juegos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void buscarPorTitulo_debeRetornar200ConResultados() throws Exception {
        JuegoResponse juego = JuegoResponse.builder().id(1L).titulo("Resident Evil 4").build();

        when(juegoService.buscarPorTitulo("Resident")).thenReturn(List.of(juego));

        mockMvc.perform(get("/api/catalogo/juegos/buscar").param("titulo", "Resident"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Resident Evil 4"));
    }

    @Test
    void actualizarDisponibilidad_debeRetornar200() throws Exception {
        JuegoResponse juego = JuegoResponse.builder().id(1L).titulo("Resident Evil 4").disponible(false).build();

        when(juegoService.actualizarDisponibilidad(1L, false)).thenReturn(juego);

        mockMvc.perform(patch("/api/catalogo/juegos/1/disponibilidad").param("disponible", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/catalogo/juegos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void existeJuego_debeRetornar200ConResultado() throws Exception {
        when(juegoService.existeJuego(1L)).thenReturn(true);

        mockMvc.perform(get("/api/catalogo/existe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true));
    }
}