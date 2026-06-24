package com.nopki.pagos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Component
public class PedidoClient {

    private static final Logger log = LoggerFactory.getLogger(PedidoClient.class);

    @Value("${servicios.pedidos.url}")
    private String pedidosUrl;

    public Map obtenerPedido(Long pedidoId) {
        try {
            log.info("Obteniendo pedido {} del servicio de pedidos", pedidoId);
            return WebClient.create(pedidosUrl)
                    .get()
                    .uri("/api/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al consultar servicio de pedidos: {}", e.getMessage());
            return null;
        }
    }

    public void actualizarEstadoPedido(Long pedidoId, String estado) {
        try {
            log.info("Actualizando estado del pedido {} a {}", pedidoId, estado);
            WebClient.create(pedidosUrl)
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/pedidos/{id}/estado")
                            .queryParam("estado", estado)
                            .build(pedidoId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al actualizar estado del pedido: {}", e.getMessage());
        }
    }

    public void asignarLicenciaAPedido(Long pedidoId, String codigoLicencia) {
        try {
            log.info("Asignando licencia {} al pedido {}", codigoLicencia, pedidoId);
            WebClient.create(pedidosUrl)
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/pedidos/{id}/licencia")
                            .queryParam("codigoLicencia", codigoLicencia)
                            .build(pedidoId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Error al asignar licencia al pedido: {}", e.getMessage());
        }
    }
}