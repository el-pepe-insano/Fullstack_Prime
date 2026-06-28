package com.nopki.pagos.controller;

import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.service.PagoService;
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

@WebMvcTest(PagosController.class)
class PagosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void procesar_debeRetornar201CuandoPagoExitoso() throws Exception {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(1L);
        request.setUsuarioId(2L);
        request.setMonto(new BigDecimal("19.99"));
        request.setMetodoPago("TARJETA_CREDITO");

        PagoResponse response = PagoResponse.builder()
                .id(1L).pedidoId(1L).usuarioId(2L).monto(new BigDecimal("19.99"))
                .estado(EstadoPago.APROBADO).codigoLicencia("NOPKI-RE04-J0K1-L2M3").build();

        when(pagoService.procesarPago(any(PagoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void obtenerPorPedido_debeRetornar200CuandoExiste() throws Exception {
        PagoResponse response = PagoResponse.builder()
                .id(1L).pedidoId(1L).estado(EstadoPago.APROBADO).build();

        when(pagoService.obtenerPorPedido(1L)).thenReturn(response);

        mockMvc.perform(get("/api/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(1));
    }

    @Test
    void listarPorUsuario_debeRetornar200ConLista() throws Exception {
        PagoResponse response = PagoResponse.builder()
                .id(1L).usuarioId(2L).estado(EstadoPago.APROBADO).build();

        when(pagoService.listarPorUsuario(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/pagos/usuario/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(2));
    }

    @Test
    void reembolsar_debeRetornar200ConEstadoReembolsado() throws Exception {
        PagoResponse response = PagoResponse.builder()
                .id(1L).estado(EstadoPago.REEMBOLSADO).build();

        when(pagoService.reembolsar(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/pagos/1/reembolsar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REEMBOLSADO"));
    }
}