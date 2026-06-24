package com.nopki.biblioteca.service;

import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import java.util.List;

public interface BibliotecaService {
    BibliotecaResponse agregar(BibliotecaRequest request);
    BibliotecaResponse obtenerPorId(Long id);
    List<BibliotecaResponse> obtenerBibliotecaUsuario(Long usuarioId);
    BibliotecaResponse obtenerJuegoDeUsuario(Long usuarioId, Long juegoId);
    boolean usuarioPoseeJuego(Long usuarioId, Long juegoId);
    long contarJuegosDeUsuario(Long usuarioId);
    void eliminar(Long id);
}