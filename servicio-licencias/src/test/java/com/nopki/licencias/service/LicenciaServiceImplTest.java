package com.nopki.licencias.service;

import com.nopki.licencias.client.CatalogoClient;
import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import com.nopki.licencias.exception.LicenciaNoEncontradaException;
import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.model.Licencia;
import com.nopki.licencias.repository.LicenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LicenciaServiceImplTest {

    @Mock
    private LicenciaRepository licenciaRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private LicenciaServiceImpl licenciaService;

    private Licencia licencia;

    @BeforeEach
    void setUp() {
        licencia = Licencia.builder()
                .id(1L)
                .juegoId(4L)
                .codigo("NOPKI-RE04-J0K1-L2M3")
                .estado(EstadoLicencia.DISPONIBLE)
                .build();
    }

    @Test
    void generarLicencias_debeCrearLicenciasCuandoJuegoExiste() {
        LicenciaRequest request = new LicenciaRequest();
        request.setJuegoId(4L);
        request.setCantidad(3);

        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(licenciaRepository.existsByCodigo(any())).thenReturn(false);
        when(licenciaRepository.save(any(Licencia.class))).thenReturn(licencia);

        List<LicenciaResponse> resultado = licenciaService.generarLicencias(request);

        assertEquals(3, resultado.size());
        verify(licenciaRepository, times(3)).save(any(Licencia.class));
    }

    @Test
    void generarLicencias_debeLanzarExcepcionCuandoJuegoNoExiste() {
        LicenciaRequest request = new LicenciaRequest();
        request.setJuegoId(999L);
        request.setCantidad(3);

        when(catalogoClient.existeJuego(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> licenciaService.generarLicencias(request));
        verify(licenciaRepository, never()).save(any(Licencia.class));
    }

    @Test
    void obtenerPorCodigo_debeRetornarLicenciaCuandoExiste() {
        when(licenciaRepository.findByCodigo("NOPKI-RE04-J0K1-L2M3")).thenReturn(Optional.of(licencia));

        LicenciaResponse response = licenciaService.obtenerPorCodigo("NOPKI-RE04-J0K1-L2M3");

        assertNotNull(response);
        assertEquals("NOPKI-RE04-J0K1-L2M3", response.getCodigo());
    }

    @Test
    void obtenerPorCodigo_debeLanzarExcepcionCuandoNoExiste() {
        when(licenciaRepository.findByCodigo("CODIGO-INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(LicenciaNoEncontradaException.class,
                () -> licenciaService.obtenerPorCodigo("CODIGO-INEXISTENTE"));
    }

    @Test
    void asignarLicencia_debeAsignarPrimeraLicenciaDisponible() {
        when(licenciaRepository.findFirstByJuegoIdAndEstado(4L, EstadoLicencia.DISPONIBLE))
                .thenReturn(Optional.of(licencia));
        when(licenciaRepository.save(any(Licencia.class))).thenReturn(licencia);

        LicenciaResponse response = licenciaService.asignarLicencia(4L, 100L);

        assertNotNull(response);
        verify(licenciaRepository, times(1)).save(any(Licencia.class));
    }

    @Test
    void asignarLicencia_debeLanzarExcepcionCuandoNoHayDisponibles() {
        when(licenciaRepository.findFirstByJuegoIdAndEstado(4L, EstadoLicencia.DISPONIBLE))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> licenciaService.asignarLicencia(4L, 100L));
    }

    @Test
    void revocarLicencia_debeRevocarCuandoNoEstaRevocada() {
        when(licenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));
        when(licenciaRepository.save(any(Licencia.class))).thenReturn(licencia);

        LicenciaResponse response = licenciaService.revocarLicencia(1L);

        assertNotNull(response);
        verify(licenciaRepository, times(1)).save(any(Licencia.class));
    }

    @Test
    void revocarLicencia_debeLanzarExcepcionCuandoYaEstaRevocada() {
        licencia.setEstado(EstadoLicencia.REVOCADA);
        when(licenciaRepository.findById(1L)).thenReturn(Optional.of(licencia));

        assertThrows(IllegalStateException.class, () -> licenciaService.revocarLicencia(1L));
    }

    @Test
    void contarDisponiblesPorJuego_debeRetornarCantidadCorrecta() {
        when(licenciaRepository.countByJuegoIdAndEstado(4L, EstadoLicencia.DISPONIBLE)).thenReturn(5L);

        long resultado = licenciaService.contarDisponiblesPorJuego(4L);

        assertEquals(5L, resultado);
    }
}