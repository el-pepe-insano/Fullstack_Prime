package com.nopki.licencias.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CatalogoClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);

    @Value("${servicios.catalogo.url}")
    private String catalogoUrl;

    public boolean existeJuego(Long juegoId) {
        try {
            log.info("Verificando existencia del juego {} en servicio de catálogo", juegoId);
            var response = WebClient.create(catalogoUrl)
                    .get()
                    .uri("/api/catalogo/existe/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(java.util.Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            log.error("Error al consultar servicio de catálogo: {}", e.getMessage());
            return false;
        }
    }
}