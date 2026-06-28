package nopki.resenas.controller;

import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import nopki.resenas.service.ResenaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResenasController.class)
class ResenasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crear_debeRetornar201CuandoDatosValidos() throws Exception {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("Resident Evil 4 es una obra maestra absoluta");

        ResenaResponse response = ResenaResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).tituloJuego("Resident Evil 4")
                .nombreUsuario("Juan Perez").calificacion(5)
                .comentario("Resident Evil 4 es una obra maestra absoluta").build();

        when(resenaService.crear(any(ResenaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void crear_debeRetornar400CuandoComentarioCorto() throws Exception {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("corto");

        mockMvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarPorJuego_debeRetornar200ConLista() throws Exception {
        ResenaResponse response = ResenaResponse.builder()
                .id(1L).juegoId(4L).calificacion(5).build();

        when(resenaService.listarPorJuego(4L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/resenas/juego/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].juegoId").value(4));
    }

    @Test
    void estadisticasJuego_debeRetornar200ConDatos() throws Exception {
        Map<String, Object> stats = Map.of("juegoId", 4L, "totalResenas", 10L, "promedioCalificacion", 4.5);

        when(resenaService.estadisticasJuego(4L)).thenReturn(stats);

        mockMvc.perform(get("/api/resenas/juego/4/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResenas").value(10));
    }

    @Test
    void actualizar_debeRetornar200CuandoEsAutor() throws Exception {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(4);
        request.setComentario("Actualizando mi opinion sobre el juego");

        ResenaResponse response = ResenaResponse.builder()
                .id(1L).usuarioId(2L).juegoId(4L).calificacion(4).build();

        when(resenaService.actualizar(eq(1L), any(ResenaRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/resenas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacion").value(4));
    }

    @Test
    void eliminar_debeRetornar204() throws Exception {
        mockMvc.perform(delete("/api/resenas/1"))
                .andExpect(status().isNoContent());
    }
}