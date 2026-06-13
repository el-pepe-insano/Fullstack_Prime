package com.nopki.pagos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class LicenciaClient {

    private static final Logger log = LoggerFactory.getLogger(LicenciaClient.class);

    @Value("${servicios.licencias.url}")
    private String licenciasUrl;

    public Map asignarLicencia(Long juegoId, Long pedidoId) {
        try {
            log.info("Solicitando licencia para juego {} y pedido {}", juegoId, pedidoId);
            return WebClient.create(licenciasUrl)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/licencias/asignar")
                            .queryParam("juegoId", juegoId)
                            .queryParam("pedidoId", pedidoId)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al asignar licencia: {}", e.getMessage());
            return null;
        }
    }
}