package nopki.resenas.service;

import nopki.resenas.dto.ResenaRequest;
import nopki.resenas.dto.ResenaResponse;
import java.util.List;
import java.util.Map;

public interface ResenaService {
    ResenaResponse crear(ResenaRequest request);
    ResenaResponse obtenerPorId(Long id);
    ResenaResponse obtenerPorUsuarioYJuego(Long usuarioId, Long juegoId);
    List<ResenaResponse> listarPorJuego(Long juegoId);
    List<ResenaResponse> listarPorJuegoOrdenadas(Long juegoId);
    List<ResenaResponse> listarPorUsuario(Long usuarioId);
    List<ResenaResponse> listarPorCalificacion(Integer calificacion);
    ResenaResponse actualizar(Long id, ResenaRequest request);
    void eliminar(Long id);
    Map<String, Object> estadisticasJuego(Long juegoId);
}