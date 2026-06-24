package nopki.resenas.service;

import nopki.resenas.client.AutenticacionClient;
import nopki.resenas.client.BibliotecaClient;
import nopki.resenas.client.CatalogoClient;
import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import nopki.resenas.exception.ResenaNoEncontradaException;
import nopki.resenas.model.Resena;
import nopki.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceImplTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private AutenticacionClient autenticacionClient;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private BibliotecaClient bibliotecaClient;

    @InjectMocks
    private ResenaServiceImpl resenaService;

    private Resena resena;

    @BeforeEach
    void setUp() {
        resena = Resena.builder()
                .id(1L)
                .usuarioId(2L)
                .juegoId(4L)
                .tituloJuego("Resident Evil 4")
                .nombreUsuario("Juan Perez")
                .calificacion(5)
                .comentario("Resident Evil 4 es una obra maestra absoluta")
                .build();
    }

    @Test
    void crear_debeCrearResenaCuandoTodasLasValidacionesPasan() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("Resident Evil 4 es una obra maestra absoluta");

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(bibliotecaClient.usuarioPoseeJuego(2L, 4L)).thenReturn(true);
        when(resenaRepository.existsByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(false);
        when(catalogoClient.obtenerTituloJuego(4L)).thenReturn("Resident Evil 4");
        when(autenticacionClient.obtenerNombreUsuario(2L)).thenReturn("Juan Perez");
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        ResenaResponse response = resenaService.crear(request);

        assertNotNull(response);
        assertEquals(5, response.getCalificacion());
        verify(resenaRepository, times(1)).save(any(Resena.class));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(999L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("Comentario de prueba valido");

        when(autenticacionClient.existeUsuario(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> resenaService.crear(request));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoJuegoNoExiste() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(999L);
        request.setCalificacion(5);
        request.setComentario("Comentario de prueba valido");

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> resenaService.crear(request));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoNoPoseeElJuego() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("Comentario de prueba valido");

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(bibliotecaClient.usuarioPoseeJuego(2L, 4L)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> resenaService.crear(request));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoYaTieneResena() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCalificacion(5);
        request.setComentario("Comentario de prueba valido");

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(bibliotecaClient.usuarioPoseeJuego(2L, 4L)).thenReturn(true);
        when(resenaRepository.existsByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> resenaService.crear(request));
    }

    @Test
    void obtenerPorUsuarioYJuego_debeRetornarResenaCuandoExiste() {
        when(resenaRepository.findByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(Optional.of(resena));

        ResenaResponse response = resenaService.obtenerPorUsuarioYJuego(2L, 4L);

        assertNotNull(response);
        assertEquals(5, response.getCalificacion());
    }

    @Test
    void obtenerPorUsuarioYJuego_debeLanzarExcepcionCuandoNoExiste() {
        when(resenaRepository.findByUsuarioIdAndJuegoId(2L, 99L)).thenReturn(Optional.empty());

        assertThrows(ResenaNoEncontradaException.class,
                () -> resenaService.obtenerPorUsuarioYJuego(2L, 99L));
    }

    @Test
    void actualizar_debeLanzarExcepcionCuandoNoEsElAutor() {
        ResenaRequest request = new ResenaRequest();
        request.setUsuarioId(999L);
        request.setCalificacion(3);
        request.setComentario("Intentando modificar reseña ajena");

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        assertThrows(IllegalStateException.class, () -> resenaService.actualizar(1L, request));
    }

    @Test
    void estadisticasJuego_debeRetornarPromedioYTotal() {
        when(resenaRepository.promedioCalificacionPorJuego(4L)).thenReturn(4.5);
        when(resenaRepository.countByJuegoId(4L)).thenReturn(10L);

        var stats = resenaService.estadisticasJuego(4L);

        assertEquals(10L, stats.get("totalResenas"));
        assertEquals(4.5, stats.get("promedioCalificacion"));
    }

    @Test
    void eliminar_debeEliminarCuandoExiste() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));

        resenaService.eliminar(1L);

        verify(resenaRepository, times(1)).deleteById(1L);
    }
}