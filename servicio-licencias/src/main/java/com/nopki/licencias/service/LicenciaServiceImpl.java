package com.nopki.licencias.service;

import com.nopki.licencias.client.CatalogoClient;
import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import com.nopki.licencias.exception.LicenciaNoEncontradaException;
import com.nopki.licencias.model.EstadoLicencia;
import com.nopki.licencias.model.Licencia;
import com.nopki.licencias.repository.LicenciaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenciaServiceImpl implements LicenciaService {

    private static final Logger log = LoggerFactory.getLogger(LicenciaServiceImpl.class);

    private final LicenciaRepository licenciaRepository;
    private final CatalogoClient catalogoClient;

    @Override
    public List<LicenciaResponse> generarLicencias(LicenciaRequest request) {
        log.info("Generando {} licencias para juego id: {}", request.getCantidad(), request.getJuegoId());

        // Regla de negocio: validar que el juego existe en el catálogo
        if (!catalogoClient.existeJuego(request.getJuegoId())) {
            throw new IllegalArgumentException("El juego con id " + request.getJuegoId() + " no existe en el catálogo");
        }

        List<Licencia> licencias = new ArrayList<>();
        for (int i = 0; i < request.getCantidad(); i++) {
            String codigo = generarCodigoUnico();
            Licencia licencia = Licencia.builder()
                    .juegoId(request.getJuegoId())
                    .codigo(codigo)
                    .build();
            licencias.add(licenciaRepository.save(licencia));
        }

        log.info("Se generaron {} licencias para juego id: {}", licencias.size(), request.getJuegoId());
        return licencias.stream().map(LicenciaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public LicenciaResponse obtenerPorId(Long id) {
        return LicenciaResponse.desde(buscarPorId(id));
    }

    @Override
    public LicenciaResponse obtenerPorCodigo(String codigo) {
        log.info("Buscando licencia por código: {}", codigo);
        return LicenciaResponse.desde(
                licenciaRepository.findByCodigo(codigo)
                        .orElseThrow(() -> new LicenciaNoEncontradaException("Licencia no encontrada con código: " + codigo))
        );
    }

    @Override
    public List<LicenciaResponse> listarPorJuego(Long juegoId) {
        log.info("Listando licencias del juego id: {}", juegoId);
        return licenciaRepository.findByJuegoId(juegoId)
                .stream().map(LicenciaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public List<LicenciaResponse> listarDisponiblesPorJuego(Long juegoId) {
        log.info("Listando licencias disponibles del juego id: {}", juegoId);
        return licenciaRepository.findByJuegoIdAndEstado(juegoId, EstadoLicencia.DISPONIBLE)
                .stream().map(LicenciaResponse::desde).collect(Collectors.toList());
    }

    @Override
    public LicenciaResponse asignarLicencia(Long juegoId, Long pedidoId) {
        log.info("Asignando licencia del juego {} al pedido {}", juegoId, pedidoId);

        // Regla de negocio: buscar primera licencia disponible
        Licencia licencia = licenciaRepository
                .findFirstByJuegoIdAndEstado(juegoId, EstadoLicencia.DISPONIBLE)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay licencias disponibles para el juego id: " + juegoId));

        licencia.setEstado(EstadoLicencia.VENDIDA);
        licencia.setFechaVenta(LocalDateTime.now());
        licencia.setPedidoId(pedidoId);

        log.info("Licencia {} asignada al pedido {}", licencia.getCodigo(), pedidoId);
        return LicenciaResponse.desde(licenciaRepository.save(licencia));
    }

    @Override
    public LicenciaResponse revocarLicencia(Long id) {
        log.info("Revocando licencia id: {}", id);
        Licencia licencia = buscarPorId(id);

        if (licencia.getEstado() == EstadoLicencia.REVOCADA) {
            throw new IllegalStateException("La licencia ya está revocada");
        }

        licencia.setEstado(EstadoLicencia.REVOCADA);
        return LicenciaResponse.desde(licenciaRepository.save(licencia));
    }

    @Override
    public long contarDisponiblesPorJuego(Long juegoId) {
        return licenciaRepository.countByJuegoIdAndEstado(juegoId, EstadoLicencia.DISPONIBLE);
    }

    @Override
    public boolean validarCodigo(String codigo) {
        return licenciaRepository.findByCodigo(codigo)
                .map(l -> l.getEstado() == EstadoLicencia.VENDIDA)
                .orElse(false);
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            // Formato: NOPKI-XXXX-XXXX-XXXX
            String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            codigo = "NOPKI-" + uuid.substring(0, 4) + "-"
                    + uuid.substring(4, 8) + "-"
                    + uuid.substring(8, 12);
        } while (licenciaRepository.existsByCodigo(codigo));
        return codigo;
    }

    private Licencia buscarPorId(Long id) {
        return licenciaRepository.findById(id)
                .orElseThrow(() -> new LicenciaNoEncontradaException("Licencia no encontrada con id: " + id));
    }
}