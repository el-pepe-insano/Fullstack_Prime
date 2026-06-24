package com.nopki.catalogo.service;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.exception.JuegoNoEncontradoException;
import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.model.Juego;
import com.nopki.catalogo.repository.JuegoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JuegoServiceImplTest {

    @Mock
    private JuegoRepository juegoRepository;

    @InjectMocks
    private JuegoServiceImpl juegoService;

    private Juego juego;

    @BeforeEach
    void setUp() {
        juego = Juego.builder()
                .id(1L)
                .titulo("Resident Evil 4")
                .descripcion("Leon S. Kennedy viaja a Europa para rescatar a la hija del presidente")
                .precio(new BigDecimal("19.99"))
                .desarrollador("Capcom")
                .plataforma("PC")
                .genero(Genero.ACCION)
                .disponible(true)
                .build();
    }

    @Test
    void crear_debeGuardarJuegoCorrectamente() {
        JuegoRequest request = new JuegoRequest();
        request.setTitulo("Resident Evil 4");
        request.setDescripcion("Leon S. Kennedy viaja a Europa para rescatar a la hija del presidente");
        request.setPrecio(new BigDecimal("19.99"));
        request.setDesarrollador("Capcom");
        request.setPlataforma("PC");
        request.setGenero(Genero.ACCION);

        when(juegoRepository.save(any(Juego.class))).thenReturn(juego);

        JuegoResponse response = juegoService.crear(request);

        assertNotNull(response);
        assertEquals("Resident Evil 4", response.getTitulo());
        verify(juegoRepository, times(1)).save(any(Juego.class));
    }

    @Test
    void obtenerPorId_debeRetornarJuegoCuandoExiste() {
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juego));

        JuegoResponse response = juegoService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals("Resident Evil 4", response.getTitulo());
    }

    @Test
    void obtenerPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(JuegoNoEncontradoException.class, () -> juegoService.obtenerPorId(99L));
    }

    @Test
    void listarDisponibles_debeRetornarSoloJuegosDisponibles() {
        when(juegoRepository.findByDisponible(true)).thenReturn(List.of(juego));

        List<JuegoResponse> resultado = juegoService.listarDisponibles();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isDisponible());
    }

    @Test
    void buscarPorRangoPrecio_debeLanzarExcepcionCuandoMinMayorQueMax() {
        BigDecimal min = new BigDecimal("50.00");
        BigDecimal max = new BigDecimal("10.00");

        assertThrows(IllegalArgumentException.class,
                () -> juegoService.buscarPorRangoPrecio(min, max));
    }

    @Test
    void actualizarDisponibilidad_debeActualizarCorrectamente() {
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juego));
        when(juegoRepository.save(any(Juego.class))).thenReturn(juego);

        JuegoResponse response = juegoService.actualizarDisponibilidad(1L, false);

        assertNotNull(response);
        verify(juegoRepository, times(1)).save(any(Juego.class));
    }

    @Test
    void eliminar_debeEliminarJuegoCuandoExiste() {
        when(juegoRepository.findById(1L)).thenReturn(Optional.of(juego));

        juegoService.eliminar(1L);

        verify(juegoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_debeLanzarExcepcionCuandoNoExiste() {
        when(juegoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(JuegoNoEncontradoException.class, () -> juegoService.eliminar(99L));
        verify(juegoRepository, never()).deleteById(any());
    }

    @Test
    void existeJuego_debeRetornarTrueCuandoExiste() {
        when(juegoRepository.existsById(1L)).thenReturn(true);

        assertTrue(juegoService.existeJuego(1L));
    }
}