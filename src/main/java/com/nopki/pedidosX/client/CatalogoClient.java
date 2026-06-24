package com.nopki.pedidosX.client;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.util.Map;

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
                    .bodyToMono(Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            log.error("Error al consultar servicio de catálogo: {}", e.getMessage());
            return false;
        }
    }

    public Map obtenerJuego(Long juegoId) {
        try {
            log.info("Obteniendo datos del juego {} del servicio de catálogo", juegoId);
            return WebClient.create(catalogoUrl)
                    .get()
                    .uri("/api/catalogo/juegos/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al obtener juego del catálogo: {}", e.getMessage());
            return null;
        }
    }

    public BigDecimal obtenerPrecioJuego(Long juegoId) {
        Map juego = obtenerJuego(juegoId);
        if (juego == null || juego.get("precio") == null) {
            throw new IllegalStateException("No se pudo obtener el precio del juego id: " + juegoId);
        }
        return new BigDecimal(juego.get("precio").toString());
    }

    public String obtenerTituloJuego(Long juegoId) {
        Map juego = obtenerJuego(juegoId);
        if (juego == null || juego.get("titulo") == null) {
            throw new IllegalStateException("No se pudo obtener el título del juego id: " + juegoId);
        }
        return juego.get("titulo").toString();
    }
}