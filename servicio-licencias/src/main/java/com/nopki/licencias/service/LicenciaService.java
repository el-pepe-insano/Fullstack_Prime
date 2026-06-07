package com.nopki.licencias.service;

import com.nopki.licencias.dto.LicenciaRequest;
import com.nopki.licencias.dto.LicenciaResponse;
import java.util.List;

public interface LicenciaService {
    List<LicenciaResponse> generarLicencias(LicenciaRequest request);
    LicenciaResponse obtenerPorId(Long id);
    LicenciaResponse obtenerPorCodigo(String codigo);
    List<LicenciaResponse> listarPorJuego(Long juegoId);
    List<LicenciaResponse> listarDisponiblesPorJuego(Long juegoId);
    LicenciaResponse asignarLicencia(Long juegoId, Long pedidoId);
    LicenciaResponse revocarLicencia(Long id);
    long contarDisponiblesPorJuego(Long juegoId);
    boolean validarCodigo(String codigo);
}