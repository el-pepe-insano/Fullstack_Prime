package com.nopki.biblioteca.service;

import com.nopki.biblioteca.client.AutenticacionClient;
import com.nopki.biblioteca.client.CatalogoClient;
import com.nopki.biblioteca.dto.BibliotecaRequest;
import com.nopki.biblioteca.dto.BibliotecaResponse;
import com.nopki.biblioteca.exception.EntradaNoEncontradaException;
import com.nopki.biblioteca.model.EntradaBiblioteca;
import com.nopki.biblioteca.repository.BibliotecaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BibliotecaServiceImpl implements BibliotecaService {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaServiceImpl.class);

    private final BibliotecaRepository bibliotecaRepository;
    private final AutenticacionClient autenticacionClient;
    private final CatalogoClient catalogoClient;

    @Override
    public BibliotecaResponse agregar(BibliotecaRequest request) {
        log.info("Agregando juego {} a biblioteca del usuario {}", request.getJuegoId(), request.getUsuarioId());

        // Regla de negocio: validar que el usuario existe
        if (!autenticacionClient.existeUsuario(request.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario con id " + request.getUsuarioId() + " no existe");
        }

        // Regla de negocio: no permitir duplicados en la biblioteca
        if (bibliotecaRepository.existsByUsuarioIdAndJuegoId(request.getUsuarioId(), request.getJuegoId())) {
            throw new IllegalStateException("El usuario ya tiene este juego en su biblioteca");
        }

        // Obtener título del juego desde el catálogo vía WebClient
        String titulo = catalogoClient.obtenerTituloJuego(request.getJuegoId());

        EntradaBiblioteca entrada = EntradaBiblioteca.builder()
                .usuarioId(request.getUsuarioId())
                .juegoId(request.getJuegoId())
                .tituloJuego(titulo)
                .codigoLicencia(request.getCodigoLicencia())
                .pedidoId(request.getPedidoId())
                .build();

        EntradaBiblioteca guardada = bibliotecaRepository.save(entrada);
        log.info("Juego {} agregado a biblioteca del usuario {}", request.getJuegoId(), request.getUsuarioId());
        return BibliotecaResponse.desde(guardada);
    }

    @Override
    public BibliotecaResponse obtenerPorId(Long id) {
        return BibliotecaResponse.desde(buscarPorId(id));
    }

    @Override
    public List<BibliotecaResponse> obtenerBibliotecaUsuario(Long usuarioId) {
        log.info("Obteniendo biblioteca del usuario {}", usuarioId);
        return bibliotecaRepository.findByUsuarioId(usuarioId)
                .stream().map(BibliotecaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public BibliotecaResponse obtenerJuegoDeUsuario(Long usuarioId, Long juegoId) {
        log.info("Buscando juego {} en biblioteca del usuario {}", juegoId, usuarioId);
        return BibliotecaResponse.desde(
                bibliotecaRepository.findByUsuarioIdAndJuegoId(usuarioId, juegoId)
                        .orElseThrow(() -> new EntradaNoEncontradaException(
                                "El usuario " + usuarioId + " no posee el juego " + juegoId))
        );
    }

    @Override
    public boolean usuarioPoseeJuego(Long usuarioId, Long juegoId) {
        return bibliotecaRepository.existsByUsuarioIdAndJuegoId(usuarioId, juegoId);
    }

    @Override
    public long contarJuegosDeUsuario(Long usuarioId) {
        return bibliotecaRepository.countByUsuarioId(usuarioId);
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando entrada de biblioteca id {}", id);
        buscarPorId(id);
        bibliotecaRepository.deleteById(id);
    }

    private EntradaBiblioteca buscarPorId(Long id) {
        return bibliotecaRepository.findById(id)
                .orElseThrow(() -> new EntradaNoEncontradaException("Entrada no encontrada con id: " + id));
    }
}