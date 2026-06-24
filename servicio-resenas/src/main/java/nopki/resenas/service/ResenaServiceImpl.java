package nopki.resenas.service;

import nopki.resenas.client.AutenticacionClient;
import nopki.resenas.client.BibliotecaClient;
import nopki.resenas.client.CatalogoClient;
import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import nopki.resenas.exception.ResenaNoEncontradaException;
import nopki.resenas.model.Resena;
import nopki.resenas.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaServiceImpl implements ResenaService {

    private static final Logger log = LoggerFactory.getLogger(ResenaServiceImpl.class);

    private final ResenaRepository resenaRepository;
    private final AutenticacionClient autenticacionClient;
    private final CatalogoClient catalogoClient;
    private final BibliotecaClient bibliotecaClient;

    @Override
    public ResenaResponse crear(ResenaRequest request) {
        log.info("Creando reseña del usuario {} para juego {}", request.getUsuarioId(), request.getJuegoId());

        // Regla de negocio 1: validar que el usuario existe
        if (!autenticacionClient.existeUsuario(request.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario con id " + request.getUsuarioId() + " no existe");
        }

        // Regla de negocio 2: validar que el juego existe en el catálogo
        if (!catalogoClient.existeJuego(request.getJuegoId())) {
            throw new IllegalArgumentException("El juego con id " + request.getJuegoId() + " no existe");
        }

        // Regla de negocio 3: el usuario debe haber comprado el juego para reseñarlo
        if (!bibliotecaClient.usuarioPoseeJuego(request.getUsuarioId(), request.getJuegoId())) {
            throw new IllegalStateException("El usuario debe comprar el juego antes de poder reseñarlo");
        }

        // Regla de negocio 4: solo se permite una reseña por usuario por juego
        if (resenaRepository.existsByUsuarioIdAndJuegoId(request.getUsuarioId(), request.getJuegoId())) {
            throw new IllegalStateException("El usuario ya tiene una reseña para este juego");
        }

        // Obtener datos de otros servicios
        String titulo = catalogoClient.obtenerTituloJuego(request.getJuegoId());
        String nombre = autenticacionClient.obtenerNombreUsuario(request.getUsuarioId());

        Resena resena = Resena.builder()
                .usuarioId(request.getUsuarioId())
                .juegoId(request.getJuegoId())
                .tituloJuego(titulo)
                .nombreUsuario(nombre)
                .calificacion(request.getCalificacion())
                .comentario(request.getComentario())
                .build();

        Resena guardada = resenaRepository.save(resena);
        log.info("Reseña {} creada para juego {}", guardada.getId(), request.getJuegoId());
        return ResenaResponse.desde(guardada);
    }

    @Override
    public ResenaResponse obtenerPorId(Long id) {
        return ResenaResponse.desde(buscarPorId(id));
    }

    @Override
    public ResenaResponse obtenerPorUsuarioYJuego(Long usuarioId, Long juegoId) {
        log.info("Buscando reseña del usuario {} para juego {}", usuarioId, juegoId);
        return ResenaResponse.desde(
                resenaRepository.findByUsuarioIdAndJuegoId(usuarioId, juegoId)
                        .orElseThrow(() -> new ResenaNoEncontradaException(
                                "No existe reseña del usuario " + usuarioId + " para el juego " + juegoId))
        );
    }

    @Override
    public List<ResenaResponse> listarPorJuego(Long juegoId) {
        log.info("Listando reseñas del juego {}", juegoId);
        return resenaRepository.findByJuegoId(juegoId)
                .stream().map(ResenaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<ResenaResponse> listarPorJuegoOrdenadas(Long juegoId) {
        log.info("Listando reseñas del juego {} ordenadas por calificación", juegoId);
        return resenaRepository.findByJuegoIdOrderByCalificacion(juegoId)
                .stream().map(ResenaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<ResenaResponse> listarPorUsuario(Long usuarioId) {
        log.info("Listando reseñas del usuario {}", usuarioId);
        return resenaRepository.findByUsuarioId(usuarioId)
                .stream().map(ResenaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<ResenaResponse> listarPorCalificacion(Integer calificacion) {
        log.info("Listando reseñas con calificación {}", calificacion);
        return resenaRepository.findByCalificacion(calificacion)
                .stream().map(ResenaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public ResenaResponse actualizar(Long id, ResenaRequest request) {
        log.info("Actualizando reseña {}", id);
        Resena resena = buscarPorId(id);

        // Regla de negocio: solo el autor puede modificar su reseña
        if (!resena.getUsuarioId().equals(request.getUsuarioId())) {
            throw new IllegalStateException("No tienes permiso para modificar esta reseña");
        }

        resena.setCalificacion(request.getCalificacion());
        resena.setComentario(request.getComentario());
        return ResenaResponse.desde(resenaRepository.save(resena));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando reseña {}", id);
        buscarPorId(id);
        resenaRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> estadisticasJuego(Long juegoId) {
        log.info("Calculando estadísticas del juego {}", juegoId);
        Double promedio = resenaRepository.promedioCalificacionPorJuego(juegoId);
        long total = resenaRepository.countByJuegoId(juegoId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("juegoId", juegoId);
        stats.put("totalResenas", total);
        stats.put("promedioCalificacion", promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0);
        return stats;
    }

    private Resena buscarPorId(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResenaNoEncontradaException("Reseña no encontrada con id: " + id));
    }
}