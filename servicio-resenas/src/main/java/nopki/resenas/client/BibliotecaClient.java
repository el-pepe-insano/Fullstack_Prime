package nopki.resenas.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class BibliotecaClient {

    private static final Logger log = LoggerFactory.getLogger(BibliotecaClient.class);

    @Value("${servicios.biblioteca.url}")
    private String bibliotecaUrl;

    public boolean usuarioPoseeJuego(Long usuarioId, Long juegoId) {
        try {
            log.info("Verificando si usuario {} posee juego {} en biblioteca", usuarioId, juegoId);
            var response = WebClient.create(bibliotecaUrl)
                    .get()
                    .uri("/api/biblioteca/usuario/{usuarioId}/posee/{juegoId}", usuarioId, juegoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("posee"));
        } catch (Exception e) {
            log.error("Error al consultar biblioteca: {}", e.getMessage());
            return false;
        }
    }
}