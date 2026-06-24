package com.nopki.pedidosX.service;

import com.nopki.pedidosX.client.AutenticacionClient;
import com.nopki.pedidosX.client.CatalogoClient;
import com.nopki.pedidosX.dto.PedidoRequest;
import com.nopki.pedidosX.dto.PedidoResponse;
import com.nopki.pedidosX.exception.PedidoNoEncontradoException;
import com.nopki.pedidosX.model.EstadoPedido;
import com.nopki.pedidosX.model.Pedido;
import com.nopki.pedidosX.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private AutenticacionClient autenticacionClient;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = Pedido.builder()
                .id(1L)
                .usuarioId(2L)
                .juegoId(4L)
                .tituloJuego("Resident Evil 4")
                .total(new BigDecimal("19.99"))
                .estado(EstadoPedido.PENDIENTE)
                .build();
    }

    @Test
    void crear_debeCrearPedidoCuandoUsuarioYJuegoExisten() {
        PedidoRequest request = new PedidoRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(pedidoRepository.existsByUsuarioIdAndJuegoIdAndEstado(2L, 4L, EstadoPedido.ENTREGADO)).thenReturn(false);
        when(catalogoClient.obtenerPrecioJuego(4L)).thenReturn(new BigDecimal("19.99"));
        when(catalogoClient.obtenerTituloJuego(4L)).thenReturn("Resident Evil 4");
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponse response = pedidoService.crear(request);

        assertNotNull(response);
        assertEquals(EstadoPedido.PENDIENTE, response.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        PedidoRequest request = new PedidoRequest();
        request.setUsuarioId(999L);
        request.setJuegoId(4L);

        when(autenticacionClient.existeUsuario(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.crear(request));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoJuegoNoExiste() {
        PedidoRequest request = new PedidoRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(999L);

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(999L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> pedidoService.crear(request));
    }

    @Test
    void crear_debeLanzarExcepcionCuandoYaCompro() {
        PedidoRequest request = new PedidoRequest();
        request.setUsuarioId(2L);
        request.setJuegoId(4L);

        when(autenticacionClient.existeUsuario(2L)).thenReturn(true);
        when(catalogoClient.existeJuego(4L)).thenReturn(true);
        when(pedidoRepository.existsByUsuarioIdAndJuegoIdAndEstado(2L, 4L, EstadoPedido.ENTREGADO)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> pedidoService.crear(request));
    }

    @Test
    void obtenerPorId_debeRetornarPedidoCuandoExiste() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponse response = pedidoService.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals("Resident Evil 4", response.getTituloJuego());
    }

    @Test
    void obtenerPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PedidoNoEncontradoException.class, () -> pedidoService.obtenerPorId(99L));
    }

    @Test
    void cancelar_debeCancelarCuandoEstaPendiente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponse response = pedidoService.cancelar(1L);

        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void cancelar_debeLanzarExcepcionCuandoYaEstaEntregado() {
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class, () -> pedidoService.cancelar(1L));
    }

    @Test
    void asignarLicencia_debeActualizarPedidoConCodigoYEstado() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        PedidoResponse response = pedidoService.asignarLicencia(1L, "NOPKI-RE04-J0K1-L2M3");

        assertNotNull(response);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
}