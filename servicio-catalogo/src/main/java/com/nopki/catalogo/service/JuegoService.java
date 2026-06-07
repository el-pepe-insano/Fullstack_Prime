package com.nopki.catalogo.service;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.model.Genero;
import java.math.BigDecimal;
import java.util.List;

public interface JuegoService {
    JuegoResponse crear(JuegoRequest request);
    JuegoResponse obtenerPorId(Long id);
    List<JuegoResponse> listarTodos();
    List<JuegoResponse> listarDisponibles();
    List<JuegoResponse> buscarPorTitulo(String titulo);
    List<JuegoResponse> listarPorGenero(Genero genero);
    List<JuegoResponse> listarPorPlataforma(String plataforma);
    List<JuegoResponse> buscarPorRangoPrecio(BigDecimal min, BigDecimal max);
    JuegoResponse actualizar(Long id, JuegoRequest request);
    JuegoResponse actualizarDisponibilidad(Long id, boolean disponible);
    void eliminar(Long id);
    boolean existeJuego(Long id);
}