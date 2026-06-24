package com.nopki.biblioteca.service;

import com.nopki.biblioteca.client.AutenticacionClient;
import com.nopki.biblioteca.client.CatalogoClient;
import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import com.nopki.biblioteca.exception.EntradaNoEncontradaException;
import com.nopki.biblioteca.model.EntradaBiblioteca;
import com.nopki.biblioteca.repository.BibliotecaRepository;
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
class BibliotecaServiceImplTest {

    @Mock
    private BibliotecaRepository bibliotecaRepository;

    @Mock
    private AutenticacionClient autenticacionClient;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private BibliotecaServiceImpl bibliotecaService;

    private EntradaBiblioteca entrada;

    @BeforeEach
    void setUp() {
        entrada = EntradaBiblioteca.builder()
                .id(1L)
                .usuarioId(2L)
                .juegoId(4L)
                .tituloJuego("Resident Evil 4")
                .codigoLicencia("NOPKI-RE04-J0K1-L2M3")
                .pedidoId(1L)
                .build();
    }

    @Test
    void agregar_debeAgregarJuegoCuandoUsuarioExisteYNoEsDuplicado() {
        BibliotecaRequest request = new BibliotecaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCodigoLicencia("NOPKI-RE04-J0K1-L2M3");
        request.setPedidoId(1L);

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(bibliotecaRepository.existsByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(false);
        when(catalogoClient.obtenerTituloJuego(4L)).thenReturn("Resident Evil 4");
        when(bibliotecaRepository.save(any(EntradaBiblioteca.class))).thenReturn(entrada);

        BibliotecaResponse response = bibliotecaService.agregar(request);

        assertNotNull(response);
        assertEquals("Resident Evil 4", response.getTituloJuego());
        verify(bibliotecaRepository, times(1)).save(any(EntradaBiblioteca.class));
    }

    @Test
    void agregar_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        BibliotecaRequest request = new BibliotecaRequest();
        request.setUsuarioId(999L);
        request.setJuegoId(4L);
        request.setCodigoLicencia("NOPKI-RE04-J0K1-L2M3");
        request.setPedidoId(1L);

        when(autenticacionClient.existeUsuario(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> bibliotecaService.agregar(request));
        verify(bibliotecaRepository, never()).save(any(EntradaBiblioteca.class));
    }

    @Test
    void agregar_debeLanzarExcepcionCuandoYaTieneElJuego() {
        BibliotecaRequest request = new BibliotecaRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);
        request.setCodigoLicencia("NOPKI-RE04-J0K1-L2M3");
        request.setPedidoId(1L);

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(bibliotecaRepository.existsByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bibliotecaService.agregar(request));
    }

    @Test
    void obtenerJuegoDeUsuario_debeRetornarEntradaCuandoExiste() {
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(Optional.of(entrada));

        BibliotecaResponse response = bibliotecaService.obtenerJuegoDeUsuario(2L, 4L);

        assertNotNull(response);
        assertEquals(2L, response.getUsuarioId());
    }

    @Test
    void obtenerJuegoDeUsuario_debeLanzarExcepcionCuandoNoExiste() {
        when(bibliotecaRepository.findByUsuarioIdAndJuegoId(2L, 999L)).thenReturn(Optional.empty());

        assertThrows(EntradaNoEncontradaException.class,
                () -> bibliotecaService.obtenerJuegoDeUsuario(2L, 999L));
    }

    @Test
    void usuarioPoseeJuego_debeRetornarTrueCuandoPosee() {
        when(bibliotecaRepository.existsByUsuarioIdAndJuegoId(2L, 4L)).thenReturn(true);

        assertTrue(bibliotecaService.usuarioPoseeJuego(2L, 4L));
    }

    @Test
    void usuarioPoseeJuego_debeRetornarFalseCuandoNoPosee() {
        when(bibliotecaRepository.existsByUsuarioIdAndJuegoId(2L, 99L)).thenReturn(false);

        assertFalse(bibliotecaService.usuarioPoseeJuego(2L, 99L));
    }

    @Test
    void contarJuegosDeUsuario_debeRetornarCantidadCorrecta() {
        when(bibliotecaRepository.countByUsuarioId(2L)).thenReturn(3L);

        long resultado = bibliotecaService.contarJuegosDeUsuario(2L);

        assertEquals(3L, resultado);
    }

    @Test
    void eliminar_debeEliminarCuandoExiste() {
        when(bibliotecaRepository.findById(1L)).thenReturn(Optional.of(entrada));

        bibliotecaService.eliminar(1L);

        verify(bibliotecaRepository, times(1)).deleteById(1L);
    }
}