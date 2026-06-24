package nopki.resenas.client;

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

    public boolean existeJuego(Long juegoId) {
        try {
            log.info("Verificando juego {} en servicio de catálogo", juegoId);
            var response = WebClient.create(catalogoUrl)
                    .get()
                    .uri("/api/catalogo/existe/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            log.error("Error al consultar catálogo: {}", e.getMessage());
            return false;
        }
    }

    public String obtenerTituloJuego(Long juegoId) {
        try {
            Map juego = WebClient.create(catalogoUrl)
                    .get()
                    .uri("/api/catalogo/juegos/{id}", juegoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return juego != null ? juego.get("titulo").toString() : "Sin título";
        } catch (Exception e) {
            log.error("Error al obtener título del juego: {}", e.getMessage());
            return "Sin título";
        }
    }
}