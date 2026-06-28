package com.nopki.licencias.controller;

import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.service.LicenciaService;
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

@WebMvcTest(LicenciasController.class)
class LicenciasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LicenciaService licenciaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generar_debeRetornar201ConListaDeLicencias() throws Exception {
        LicenciaRequest request = new LicenciaRequest();
        request.setJuegoId(4L);
        request.setCantidad(3);

        LicenciaResponse licencia = LicenciaResponse.builder()
                .id(1L).juegoId(4L).codigo("NOPKI-RE04-J0K1-L2M3").estado(EstadoLicencia.DISPONIBLE).build();

        when(licenciaService.generarLicencias(any(LicenciaRequest.class))).thenReturn(List.of(licencia, licencia, licencia));

        mockMvc.perform(post("/api/licencias/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void generar_debeRetornar400CuandoCantidadInvalida() throws Exception {
        LicenciaRequest request = new LicenciaRequest();
        request.setJuegoId(4L);
        request.setCantidad(0);

        mockMvc.perform(post("/api/licencias/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorCodigo_debeRetornar200CuandoExiste() throws Exception {
        LicenciaResponse licencia = LicenciaResponse.builder()
                .id(1L).juegoId(4L).codigo("NOPKI-RE04-J0K1-L2M3").estado(EstadoLicencia.VENDIDA).build();

        when(licenciaService.obtenerPorCodigo("NOPKI-RE04-J0K1-L2M3")).thenReturn(licencia);

        mockMvc.perform(get("/api/licencias/codigo/NOPKI-RE04-J0K1-L2M3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("NOPKI-RE04-J0K1-L2M3"));
    }

    @Test
    void contarStock_debeRetornar200ConCantidad() throws Exception {
        when(licenciaService.contarDisponiblesPorJuego(4L)).thenReturn(5L);

        mockMvc.perform(get("/api/licencias/juego/4/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponibles").value(5));
    }

    @Test
    void asignar_debeRetornar200ConLicenciaAsignada() throws Exception {
        LicenciaResponse licencia = LicenciaResponse.builder()
                .id(1L).juegoId(4L).codigo("NOPKI-RE04-J0K1-L2M3").estado(EstadoLicencia.VENDIDA).pedidoId(10L).build();

        when(licenciaService.asignarLicencia(4L, 10L)).thenReturn(licencia);

        mockMvc.perform(post("/api/licencias/asignar")
                        .param("juegoId", "4")
                        .param("pedidoId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(10));
    }

    @Test
    void revocar_debeRetornar200ConLicenciaRevocada() throws Exception {
        LicenciaResponse licencia = LicenciaResponse.builder()
                .id(1L).codigo("NOPKI-RE04-J0K1-L2M3").estado(EstadoLicencia.REVOCADA).build();

        when(licenciaService.revocarLicencia(1L)).thenReturn(licencia);

        mockMvc.perform(patch("/api/licencias/1/revocar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REVOCADA"));
    }

    @Test
    void validar_debeRetornar200ConResultado() throws Exception {
        when(licenciaService.validarCodigo("NOPKI-RE04-J0K1-L2M3")).thenReturn(true);

        mockMvc.perform(get("/api/licencias/validar").param("codigo", "NOPKI-RE04-J0K1-L2M3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }
}