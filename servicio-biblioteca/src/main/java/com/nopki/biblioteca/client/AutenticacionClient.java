package com.nopki.biblioteca.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class AutenticacionClient {

    private static final Logger log = LoggerFactory.getLogger(AutenticacionClient.class);

    @Value("${servicios.autenticacion.url}")
    private String autenticacionUrl;

    public boolean existeUsuario(Long usuarioId) {
        try {
            log.info("Verificando usuario {} en servicio de autenticación", usuarioId);
            var response = WebClient.create(autenticacionUrl)
                    .get()
                    .uri("/api/auth/existe/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && Boolean.TRUE.equals(response.get("existe"));
        } catch (Exception e) {
            log.error("Error al consultar servicio de autenticación: {}", e.getMessage());
            return false;
        }
    }
}