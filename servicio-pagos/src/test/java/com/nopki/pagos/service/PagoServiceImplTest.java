package com.nopki.pagos.service;

import com.nopki.pagos.client.LicenciaClient;
import com.nopki.pagos.client.PedidoClient;
import com.nopki.pagos.dto.PagoRequest;
import com.nopki.pagos.dto.PagoResponse;
import com.nopki.pagos.exception.PagoNoEncontradoException;
import com.nopki.pagos.model.EstadoPago;
import com.nopki.pagos.model.Pago;
import com.nopki.pagos.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private LicenciaClient licenciaClient;

    @InjectMocks
    private PagoServiceImpl pagoService;

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = Pago.builder()
                .id(1L)
                .pedidoId(1L)
                .usuarioId(2L)
                .monto(new BigDecimal("19.99"))
                .metodoPago("TARJETA_CREDITO")
                .estado(EstadoPago.APROBADO)
                .build();
    }

    @Test
    void procesarPago_debeAprobarPagoYAsignarLicencia() {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(1L);
        request.setUsuarioId(2L);
        request.setMonto(new BigDecimal("19.99"));
        request.setMetodoPago("TARJETA_CREDITO");

        Map<String, Object> pedidoMock = Map.of("estado", "PENDIENTE", "juegoId", "4");
        Map<String, Object> licenciaMock = Map.of("codigo", "NOPKI-RE04-J0K1-L2M3");

        when(pagoRepository.existsByPedidoId(1L)).thenReturn(false);
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoMock);
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);
        when(licenciaClient.asignarLicencia(4L, 1L)).thenReturn(licenciaMock);

        PagoResponse response = pagoService.procesarPago(request);

        assertNotNull(response);
        assertEquals(EstadoPago.APROBADO, response.getEstado());
        verify(licenciaClient, times(1)).asignarLicencia(4L, 1L);
        verify(pedidoClient, times(1)).asignarLicenciaAPedido(1L, "NOPKI-RE04-J0K1-L2M3");
    }

    @Test
    void procesarPago_debeLanzarExcepcionCuandoPedidoYaTienePago() {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(1L);
        request.setUsuarioId(2L);
        request.setMonto(new BigDecimal("19.99"));
        request.setMetodoPago("TARJETA_CREDITO");

        when(pagoRepository.existsByPedidoId(1L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> pagoService.procesarPago(request));
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void procesarPago_debeLanzarExcepcionCuandoPedidoNoExiste() {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(999L);
        request.setUsuarioId(2L);
        request.setMonto(new BigDecimal("19.99"));
        request.setMetodoPago("TARJETA_CREDITO");

        when(pagoRepository.existsByPedidoId(999L)).thenReturn(false);
        when(pedidoClient.obtenerPedido(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> pagoService.procesarPago(request));
    }

    @Test
    void procesarPago_debeLanzarExcepcionCuandoPedidoNoEstaPendiente() {
        PagoRequest request = new PagoRequest();
        request.setPedidoId(1L);
        request.setUsuarioId(2L);
        request.setMonto(new BigDecimal("19.99"));
        request.setMetodoPago("TARJETA_CREDITO");

        Map<String, Object> pedidoMock = Map.of("estado", "CANCELADO", "juegoId", "4");

        when(pagoRepository.existsByPedidoId(1L)).thenReturn(false);
        when(pedidoClient.obtenerPedido(1L)).thenReturn(pedidoMock);

        assertThrows(IllegalStateException.class, () -> pagoService.procesarPago(request));
    }

    @Test
    void obtenerPorPedido_debeRetornarPagoCuandoExiste() {
        when(pagoRepository.findByPedidoId(1L)).thenReturn(Optional.of(pago));

        PagoResponse response = pagoService.obtenerPorPedido(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPedidoId());
    }

    @Test
    void obtenerPorPedido_debeLanzarExcepcionCuandoNoExiste() {
        when(pagoRepository.findByPedidoId(999L)).thenReturn(Optional.empty());

        assertThrows(PagoNoEncontradoException.class, () -> pagoService.obtenerPorPedido(999L));
    }

    @Test
    void reembolsar_debeReembolsarCuandoEstaAprobado() {
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        PagoResponse response = pagoService.reembolsar(1L);

        assertNotNull(response);
        verify(pedidoClient, times(1)).actualizarEstadoPedido(1L, "CANCELADO");
    }

    @Test
    void reembolsar_debeLanzarExcepcionCuandoNoEstaAprobado() {
        pago.setEstado(EstadoPago.RECHAZADO);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        assertThrows(IllegalStateException.class, () -> pagoService.reembolsar(1L));
    }
}