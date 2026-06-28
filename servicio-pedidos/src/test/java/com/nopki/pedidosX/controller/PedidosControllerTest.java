package com.nopki.pedidosX.controller;

import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.service.PedidoService;
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

@WebMvcTest(PedidosController.class)
class PedidosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PedidoService pedidoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crear_debeRetornar201CuandoDatosValidos() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);

        PedidoResponse response = PedidoResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).tituloJuego("Resident Evil 4")
                .total(new BigDecimal("19.99")).estado(EstadoPedido.PENDIENTE).build();

        when(pedidoService.crear(any(PedidoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void obtenerPorId_debeRetornar200CuandoExiste() throws Exception {
        PedidoResponse response = PedidoResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).tituloJuego("Resident Evil 4").build();

        when(pedidoService.obtenerPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tituloJuego").value("Resident Evil 4"));
    }

    @Test
    void historialUsuario_debeRetornar200ConLista() throws Exception {
        PedidoResponse response = PedidoResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).estado(EstadoPedido.ENTREGADO).build();

        when(pedidoService.historialUsuario(2L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/pedidos/usuario/2/historial"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(2));
    }

    @Test
    void actualizarEstado_debeRetornar200() throws Exception {
        PedidoResponse response = PedidoResponse.builder()
                .id(1L).estado(EstadoPedido.PAGADO).build();

        when(pedidoService.actualizarEstado(1L, EstadoPedido.PAGADO)).thenReturn(response);

        mockMvc.perform(patch("/api/pedidos/1/estado").param("estado", "PAGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PAGADO"));
    }

    @Test
    void asignarLicencia_debeRetornar200() throws Exception {
        PedidoResponse response = PedidoResponse.builder()
                .id(1L).estado(EstadoPedido.ENTREGADO).codigoLicencia("NOPKI-RE04-J0K1-L2M3").build();

        when(pedidoService.asignarLicencia(1L, "NOPKI-RE04-J0K1-L2M3")).thenReturn(response);

        mockMvc.perform(patch("/api/pedidos/1/licencia").param("codigoLicencia", "NOPKI-RE04-J0K1-L2M3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoLicencia").value("NOPKI-RE04-J0K1-L2M3"));
    }

    @Test
    void cancelar_debeRetornar200() throws Exception {
        PedidoResponse response = PedidoResponse.builder()
                .id(1L).estado(EstadoPedido.CANCELADO).build();

        when(pedidoService.cancelar(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/pedidos/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }
}