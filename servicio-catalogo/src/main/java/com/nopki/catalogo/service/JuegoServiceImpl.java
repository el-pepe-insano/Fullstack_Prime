package com.nopki.catalogo.service;

import com.nopki.catalogo.dto.JuegoRequest;
import com.nopki.catalogo.dto.JuegoResponse;
import com.nopki.catalogo.exception.JuegoNoEncontradoException;
import com.nopki.catalogo.model.Genero;
import com.nopki.catalogo.model.Juego;
import com.nopki.catalogo.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JuegoServiceImpl implements JuegoService {

    private static final Logger log = LoggerFactory.getLogger(JuegoServiceImpl.class);

    private final JuegoRepository juegoRepository;

    @Override
    public JuegoResponse crear(JuegoRequest request) {
        log.info("Creando juego: {}", request.getTitulo());
        Juego juego = Juego.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .desarrollador(request.getDesarrollador())
                .plataforma(request.getPlataforma())
                .genero(request.getGenero())
                .imagenUrl(request.getImagenUrl())
                .fechaLanzamiento(request.getFechaLanzamiento())
                .build();
        return JuegoResponse.desde(juegoRepository.save(juego));
    }

    @Override
    public JuegoResponse obtenerPorId(Long id) {
        log.info("Buscando juego con id: {}", id);
        return JuegoResponse.desde(buscarPorId(id));
    }

    @Override
    public List<JuegoResponse> listarTodos() {
        log.info("Listando todos los juegos");
        return juegoRepository.findAll()
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> listarDisponibles() {
        log.info("Listando juegos disponibles");
        return juegoRepository.findByDisponible(true)
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> buscarPorTitulo(String titulo) {
        log.info("Buscando juegos por título: {}", titulo);
        return juegoRepository.buscarPorTitulo(titulo)
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> listarPorGenero(Genero genero) {
        log.info("Listando juegos por género: {}", genero);
        return juegoRepository.buscarDisponiblesPorGenero(genero)
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> listarPorPlataforma(String plataforma) {
        log.info("Listando juegos por plataforma: {}", plataforma);
        return juegoRepository.findByPlataforma(plataforma)
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> buscarPorRangoPrecio(BigDecimal min, BigDecimal max) {
        log.info("Buscando juegos entre {} y {}", min, max);
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("El precio mínimo no puede ser mayor al máximo");
        }
        return juegoRepository.buscarPorRangoPrecio(min, max)
                .stream().map(JuegoResponse::desde).collect(Collectors.toList());
    }

    @Override
    public JuegoResponse actualizar(Long id, JuegoRequest request) {
        log.info("Actualizando juego con id: {}", id);
        Juego juego = buscarPorId(id);
        juego.setTitulo(request.getTitulo());
        juego.setDescripcion(request.getDescripcion());
        juego.setPrecio(request.getPrecio());
        juego.setDesarrollador(request.getDesarrollador());
        juego.setPlataforma(request.getPlataforma());
        juego.setGenero(request.getGenero());
        juego.setImagenUrl(request.getImagenUrl());
        juego.setFechaLanzamiento(request.getFechaLanzamiento());
        return JuegoResponse.desde(juegoRepository.save(juego));
    }

    @Override
    public JuegoResponse actualizarDisponibilidad(Long id, boolean disponible) {
        log.info("Actualizando disponibilidad del juego {} a: {}", id, disponible);
        Juego juego = buscarPorId(id);
        juego.setDisponible(disponible);
        return JuegoResponse.desde(juegoRepository.save(juego));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando juego con id: {}", id);
        buscarPorId(id);
        juegoRepository.deleteById(id);
    }

    @Override
    public boolean existeJuego(Long id) {
        return juegoRepository.existsById(id);
    }

    private Juego buscarPorId(Long id) {
        return juegoRepository.findById(id)
                .orElseThrow(() -> new JuegoNoEncontradoException("Juego no encontrado con id: " + id));
    }
}