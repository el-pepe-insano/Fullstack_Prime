package com.nopki.biblioteca.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class CatalogoClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);

    @Value("${servicios.catalogo.url}")
    private String catalogoUrl;

    public String obtenerTituloJuego(Long juegoId) {
        try {
            log.info("Obteniendo título del juego {} del servicio de catálogo", juegoId);
            Map juego = WebClient.create(catalogoUrl)
                    .get()
                    .uri("/api/catalogo/juegos/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return juego != null ? juego.get("titulo").toString() : "Sin título";
        } catch (Exception e) {
            log.error("Error al consultar servicio de catálogo: {}", e.getMessage());
            return "Sin título";
        }
    }
}